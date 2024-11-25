package com.example.iamservice.service.impl;

import com.example.iamservice.constant.KafkaTopicConstants;
import com.example.iamservice.dto.request.email.EmailRequest;
import com.example.iamservice.dto.request.signup.SignUpRequest;
import com.example.iamservice.entity.User;
import com.example.iamservice.enums.MessageCode;
import com.example.iamservice.exception.handler.BadRequestAlertException;
import com.example.iamservice.repository.UserRepository;
import com.example.iamservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void signUp(SignUpRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestAlertException(MessageCode.MSG1012);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setStatus(true);
        user.setIsVerified(false);
        userRepository.save(user);


    }
}
