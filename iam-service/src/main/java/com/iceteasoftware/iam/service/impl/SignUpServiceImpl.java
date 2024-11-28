package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.client.UserClient;
import com.iceteasoftware.iam.constant.KafkaTopicConstants;
import com.iceteasoftware.iam.dto.request.signup.CreateProfileRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.entity.PasswordHistory;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.PasswordHistoryRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.SignUpService;
import com.iceteasoftware.iam.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpServiceImpl implements SignUpService {

    private final UserRepository userRepository;
    private final UserClient userClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;
    private final PasswordHistoryRepository passwordHistoryRepository;

    /**
     * Handles user sign-up by validating the input, creating a user, and setting up the user's profile.
     *
     * <p>This method performs the following tasks:</p>
     *
     *     <li>Validates input fields such as first name, last name, email, password, and phone number.</li>
     *     <li>Checks for duplicate email and phone number in the database.</li>
     *     <li>Encodes the user's password and saves it to the database.</li>
     *     <li>Creates a profile for the user via a remote service.</li>
     * </ul>
     *
     * @param request the {@link SignUpRequest} object containing user information for sign-up.
     * @throws BadRequestAlertException if any validation fails or if a duplicate email/phone number is found.
     */
    @Override
    public void signUp(SignUpRequest request) {
        if(Validator.isBlankOrEmpty(request.getFirstName())) {
            throw new BadRequestAlertException(MessageCode.MSG1053);
        }

        if(Validator.isBlankOrEmpty(request.getFirstName())) {
            throw new BadRequestAlertException(MessageCode.MSG1054);
        }

        if(Validator.isFullName(request.getFirstName())){
            throw new BadRequestAlertException(MessageCode.MSG1052);
        }

        if(Validator.isFullName(request.getLastName())){
            throw new BadRequestAlertException(MessageCode.MSG1051);
        }

        if(Validator.isBlankOrEmpty(request.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1003);
        }

        if(Validator.isEmail(request.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        }

        if(Validator.isBlankOrEmpty(request.getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1001);
        }

        if(!Validator.isPasswordRegex(request.getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1004);
        }

        if(Validator.isBlankOrEmpty(request.getPhone())) {
            throw new BadRequestAlertException(MessageCode.MSG1045);
        }

        if(!Validator.isVNPhoneNumber(request.getPhone())){
            throw new BadRequestAlertException(MessageCode.MSG1044);
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestAlertException(MessageCode.MSG1012);
        }

        if(userClient.checkPhoneExists(request.getPhone())) {
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
                .       build());

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .dateOfBirth(request.getDateOfBirth())
                    .email(request.getEmail())
                    .lastName(request.getLastName())
                    .firstName(request.getFirstName())
                    .build();

        ResponseObject<String> createProfile = userClient.createProfile(createProfileRequest);
        log.info("createProfile: {}", createProfile.toString());
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
     * @param email the email address for which the OTP is generated.
     */
    @Override
    public void generateOtp(String email) {
        String otp = generateOtpString();
        redisTemplate.opsForValue().set(email, otp, 2, TimeUnit.MINUTES);

        String message = String.format("{\"email\": \"%s\", \"otp\": \"%s\"}", email, otp);
        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP, message);
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

        if(storedOtp == null) {
            throw new BadRequestAlertException(MessageCode.MSG1017);
        }

        if(!storedOtp.equals(request.getOtp())) {
            throw new BadRequestAlertException(MessageCode.MSG1017);
        }

        redisTemplate.delete(request.getEmail());
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> user.setIsVerified("VERIFIED"));
    }

    private String generateOtpString(){
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }
}
