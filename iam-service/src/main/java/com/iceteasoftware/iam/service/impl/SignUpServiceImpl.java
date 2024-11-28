package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.client.UserClient;
import com.iceteasoftware.iam.constant.KafkaTopicConstants;
import com.iceteasoftware.iam.dto.request.signup.CreateProfileRequest;
import com.iceteasoftware.iam.dto.request.signup.SignUpRequest;
import com.iceteasoftware.iam.dto.request.signup.VerifyUserRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.SignUpService;
import com.iceteasoftware.iam.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

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

        CreateProfileRequest createProfileRequest = new CreateProfileRequest();
        createProfileRequest.setAddress(request.getAddress());
        createProfileRequest.setPhone(request.getPhone());
        createProfileRequest.setFirstName(request.getFirstName());
        createProfileRequest.setLastName(request.getLastName());
        createProfileRequest.setDateOfBirth(request.getDateOfBirth());
        createProfileRequest.setEmail(request.getEmail());
        createProfileRequest.setUserId(user.getUserId());

        ResponseObject<String> createProfile = userClient.createProfile(createProfileRequest);
        log.info("createProfile: {}", createProfile.toString());
    }

    @Override
    public void generateOtp(String email) {
        String otp = generateOtpString();
        redisTemplate.opsForValue().set(email, otp, 2, TimeUnit.MINUTES);

        String message = String.format("{\"email\": \"%s\", \"otp\": \"%s\"}", email, otp);
        kafkaTemplate.send(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_SIGN_UP, message);
    }

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
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if(user.isPresent()) {
            user.get().setIsVerified("VERIFIED");
            userRepository.save(user.get());
        }
    }

    private String generateOtpString(){
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }
}
