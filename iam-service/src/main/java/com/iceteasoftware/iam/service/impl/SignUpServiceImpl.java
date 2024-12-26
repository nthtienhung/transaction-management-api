package com.iceteasoftware.iam.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.iceteasoftware.iam.client.UserClient;
import com.iceteasoftware.iam.configuration.kafka.KafkaProducer;
import com.iceteasoftware.iam.constant.KafkaTopicConstants;
import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.OTPRequest;
import com.iceteasoftware.iam.dto.request.email.EmailDTORequest;
import com.iceteasoftware.iam.dto.request.signup.CreateProfileRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;
import com.iceteasoftware.iam.dto.request.wallet.CreateWalletRequest;
import com.iceteasoftware.iam.entity.PasswordHistory;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.PasswordHistoryRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.SignUpService;
import com.iceteasoftware.common.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpServiceImpl implements SignUpService {

    private final UserRepository userRepository;
    private final UserClient userClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final KafkaProducer kafkaProducer;
    private static final String IS_VERIFIED = "VERIFIED";
    private static final String NOT_VERIFIED = "NOT_VERIFIED";

    /**
     * Handles the user sign-up process by validating inputs, creating a user record,
     * and setting up associated resources such as user profile and wallet.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Validates required fields: first name, last name, email, password, and phone number.</li>
     *     <li>Ensures the first name and last name follow specified format rules.</li>
     *     <li>Verifies the email address and phone number adhere to proper formats.</li>
     *     <li>Checks for duplicate email addresses in the database and duplicate phone numbers via an external service.</li>
     *     <li>Encodes the user's password before saving it to the database.</li>
     *     <li>Creates a user profile by sending a message to the appropriate Kafka topic.</li>
     *     <li>Initializes a wallet for the user with a default balance by sending a message to another Kafka topic.</li>
     * </ul>
     *
     * @param request the {@link SignUpRequest} object containing user-provided information for sign-up.
     *                This includes fields such as first name, last name, email, password, phone number, address, and date of birth.
     * @throws BadRequestAlertException if:
     *                                  <ul>
     *                                      <li>A required field is blank or empty.</li>
     *                                      <li>The input data does not meet validation rules (e.g., incorrect email or password format).</li>
     *                                      <li>A duplicate email address or phone number is detected.</li>
     *                                  </ul>
     */
    @Override
    public void signUp(SignUpRequest request) throws JsonProcessingException {
        if (Validator.isBlankOrEmpty(request.getFirstName())) {
            throw new BadRequestAlertException(MessageCode.MSG1053);
        }

        if (Validator.isBlankOrEmpty(request.getFirstName())) {
            throw new BadRequestAlertException(MessageCode.MSG1054);
        }

        if (Validator.isFullName(request.getFirstName())) {
            throw new BadRequestAlertException(MessageCode.MSG1052);
        }

        if (Validator.isFullName(request.getLastName())) {
            throw new BadRequestAlertException(MessageCode.MSG1051);
        }

        if (Validator.isBlankOrEmpty(request.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1003);
        }

        if (Validator.isEmail(request.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        }

        if (Validator.isBlankOrEmpty(request.getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1001);
        }

        if (!Validator.isPasswordRegex(request.getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1004);
        }

        if (Validator.isBlankOrEmpty(request.getPhone())) {
            throw new BadRequestAlertException(MessageCode.MSG1045);
        }

        if (!Validator.isVNPhoneNumber(request.getPhone())) {
            throw new BadRequestAlertException(MessageCode.MSG1044);
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (user.getIsVerified().equals(IS_VERIFIED)) {
                throw new BadRequestAlertException(MessageCode.MSG1012);
            }

            if (user.getIsVerified().equals(NOT_VERIFIED)) {
                throw new BadRequestAlertException(MessageCode.MSG1056);
            }
        }

        if (userClient.checkPhoneExists(request.getPhone())) {
            throw new BadRequestAlertException(MessageCode.MSG1055);
        }
        User user = userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .isVerified("NOT_VERIFIED")
                .status(true)
                .build());

        passwordHistoryRepository.save(PasswordHistory.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build());

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
                .phone(request.getPhone())
                .address(request.getAddress())
                .dob(request.getDateOfBirth())
                .email(request.getEmail())
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .userId(user.getUserId())
                .build();
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_PROFILE, createProfileRequest);

        CreateWalletRequest createWalletRequest = CreateWalletRequest.builder()
                .userId(user.getUserId())
                .balance(5000000L)
                .build();
        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_WALLET, createWalletRequest);
    }

    /**
     * Generates a One-Time Password (OTP) for the provided email and sends it via Kafka for email delivery.
     *
     * <p>This method performs the following tasks:</p>
     * <ul>
     *     <li>Generates an OTP string.</li>
     *     <li>Stores the OTP in Redis with a 2-minute expiration time.</li>
     *     <li>Sends the OTP to the default Kafka topic for email notifications.</li>
     * </ul>
     *
     * @param request {@link EmailRequest} email the email address for which the OTP is generated.
     * @throws BadRequestAlertException if the user is not found in the database.
     */
    @Override
    public void generateOtp(EmailRequest request) throws JsonProcessingException {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1016);
        }

        String otp = generateOtpString();
        redisTemplate.opsForValue().set(request.getEmail(), otp, 2, TimeUnit.MINUTES);

        OTPRequest data = OTPRequest.builder().email(user.get().getEmail()).otp(otp).build();
        EmailDTORequest emailDTO = new EmailDTORequest();
        emailDTO.setUserId(user.get().getUserId());
        emailDTO.setData(new Gson().toJson(data));
        emailDTO.setTopicName(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP);

        kafkaProducer.sendMessage(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP, emailDTO);
    }

    /**
     * Verifies a user's OTP for account activation.
     *
     * <p>This method performs the following tasks:</p>
     * <ul>
     *     <li>Retrieves the stored OTP from Redis for the provided email.</li>
     *     <li>Validates the provided OTP against the stored OTP.</li>
     *     <li>If successful, deletes the OTP from Redis and marks the user as verified in the database.</li>
     * </ul>
     *
     * @param request the {@link VerifyUserRequest} containing the user's email and OTP for verification.
     * @throws BadRequestAlertException if the OTP is invalid or expired.
     */
    @Override
    public void verifyUser(VerifyUserRequest request) {
        String storedOtp = redisTemplate.opsForValue().get(request.getEmail());

        if (storedOtp == null) {
            throw new BadRequestAlertException(MessageCode.MSG1017);
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new BadRequestAlertException(MessageCode.MSG1017);
        }

        redisTemplate.delete(request.getEmail());

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1016);
        }

        user.get().setIsVerified("VERIFIED");
        userRepository.save(user.get());

    }

    private String generateOtpString() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);

        return String.valueOf(otp);
    }
}
