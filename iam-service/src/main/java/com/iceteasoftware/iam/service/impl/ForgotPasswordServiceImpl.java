package com.iceteasoftware.iam.service.impl;


import com.iceteasoftware.iam.configuration.kafka.KafkaProducer;
import com.iceteasoftware.iam.constant.Constants;
import com.iceteasoftware.iam.constant.KafkaTopicConstants;
import com.iceteasoftware.iam.dto.request.EmailRequest;
import com.iceteasoftware.iam.dto.request.OTPRequest;
import com.iceteasoftware.iam.dto.request.ResetPasswordRequest;
import com.iceteasoftware.iam.dto.request.email.EmailDTORequest;
import com.iceteasoftware.iam.dto.response.OTPResponse;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.model.OTPValue;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.ForgotPasswordService;
import com.iceteasoftware.iam.util.DateUtil;
import com.iceteasoftware.iam.util.Validator;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.iceteasoftware.iam.constant.Constants.*;

@Transactional
@Service
@RequiredArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private final UserRepository userRepository;

    private final KafkaProducer kafkaProducer;

    private final RedisTemplate<String, Object> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    private static final String OTP_PREFIX = "OTP:";


    /**
     * Verify email and send OTP + email to user's email.
     *
     * @param email The EmailRequest containing user's email input.
     * @return OTP and expiration time.
     */
    @Override
    public void verifyMail(EmailRequest email) {
        Optional<User> user = userRepository.findByEmail(email.getEmail());
        int emailLength = email.getEmail().length() ;
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1016);
        } else if (!Validator.isEmailAddress(email.getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        } else if (emailLength < DEFAULT_EMAIL_LENGTH_MIN || emailLength > DEFAULT_EMAIL_LENGTH_MAX) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        } else if (Validator.isBlankOrEmpty(user.get().getEmail())) {
            throw new BadRequestAlertException(MessageCode.MSG1003);
        }


    }

    /**
     *
     * @param request
     * @return
     */
    @Override
    public String resetPassword(ResetPasswordRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        int passwordLength = request.getNewPassword().length();
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        }else  if (Validator.isBlankOrEmpty(request.getNewPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1001);
        } else if (Validator.isPassword(request.getNewPassword())
                || passwordLength < Constants.DEFAULT_PASSWORD_LENGTH_MIN
                || passwordLength > Constants.DEFAULT_PASSWORD_LENGTH_MAX) {
            throw new BadRequestAlertException(MessageCode.MSG1004);
        } else if (Validator.isBlankOrEmpty(request.getConfirmPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1032);
        } else if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1031);
        } else if (passwordEncoder.matches(request.getNewPassword(), user.get().getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1033);
        }

        user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user.get());
        redisTemplate.delete(request.getEmail());
        return Constants.DEFAULT_MESSAGE_UPDATE_SUCCESS;
    }

    /**
     * Generate OTP and send to user's email.
     * @param email
     */
    @Override
    public void generateOtp(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1016);
        }

        // Tạo OTP
        Date expirationTime = DateUtil.getMinutesAfter(DEFAULT_OTP_EXPIRE_TIME);
        String otp = generateOtpString();

        // Lưu OTP vào Redis
        OTPValue existingValue = getOtpFromRedis(email);
        int count = existingValue != null ? existingValue.getCount() + 1 : 0;
        setOtpInRedis(email, otp, expirationTime, count);

        // Gửi email OTP
        OTPRequest data = OTPRequest.builder().email(user.get().getEmail()).otp(otp).build();
        EmailDTORequest emailDTO = new EmailDTORequest();
        emailDTO.setUserId(user.get().getUserId());
        emailDTO.setData(new Gson().toJson(data));
        emailDTO.setTopicName(KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_SEND_EMAIL_FORGOT_PASSWORD);

        kafkaProducer.sendMessageEmail(emailDTO);
    }

    private String generateOtpString(){
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public OTPResponse verifyOTP(OTPRequest request) {
        OTPValue value = getOtpFromRedis(request.getEmail());

        if (value == null) {
            throw new BadRequestAlertException(MessageCode.MSG1030);
        } else if (Validator.isBlankOrEmpty(request.getOtp())) {
            throw new BadRequestAlertException(MessageCode.MSG1027);
        } else if (Validator.isOTP(request.getOtp())
                || request.getOtp().length() != DEFAULT_OTP_LENGTH) {
            throw new BadRequestAlertException(MessageCode.MSG1026);
        } else if (!request.getOtp().equals(value.getOtp())) {
            if (value.getOtpConfirmCount() >= DEFAULT_OTP_CONFIRM_TIME) {
                deleteOtpFromRedis(request.getEmail());
                throw new BadRequestAlertException(MessageCode.MSG1043);
            } else {
                setOtpInRedis(request.getEmail(), value.getOtp(), value.getExpirationTime(), value.getCount() + 1);
            }
            throw new BadRequestAlertException(MessageCode.MSG1028);
        } else if (DateUtil.compareTo(value.getExpirationTime(), Date.from(Instant.now()), false) == -1) {
            throw new BadRequestAlertException(MessageCode.MSG1029);
        }

        // Xóa OTP sau khi dùng
        deleteOtpFromRedis(request.getEmail());

        String resetPasswordKey = UUID.randomUUID().toString();

        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1016);
        }

        userRepository.save(user.get());

        return new OTPResponse(request.getEmail(), resetPasswordKey);
    }

    private void setOtpInRedis(String email, String otp, Date expirationTime, int count) {
        OTPValue otpValue = new OTPValue(otp, expirationTime, count);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(OTP_PREFIX + email, otpValue, DEFAULT_OTP_EXPIRE_TIME, TimeUnit.MINUTES);
    }
    private OTPValue getOtpFromRedis(String email) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (OTPValue) valueOperations.get(OTP_PREFIX + email);
    }
    private void deleteOtpFromRedis(String email) {
        redisTemplate.delete(OTP_PREFIX + email);
    }

}
