package com.example.iamservice.service.impl;

import com.example.iamservice.configuration.message.Labels;
import com.example.iamservice.dto.request.changepassword.ChangePasswordRequest;
import com.example.iamservice.dto.response.ResponseData;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.entity.PasswordHistory;
import com.example.iamservice.entity.User;
import com.example.iamservice.enums.MessageCode;
import com.example.iamservice.exception.handler.BadRequestAlertException;
import com.example.iamservice.repository.PasswordHistoryRepository;
import com.example.iamservice.repository.UserRepository;
import com.example.iamservice.service.ChangePasswordService;
import com.example.iamservice.util.Validator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final UserRepository userRepository;

    private final PasswordHistoryRepository passwordHistoryRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<ResponseObject> changePasswordByEmail(ChangePasswordRequest request){
        Optional<User> optionalUser = this.userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1035);
        } else if (request.getOldPassword() == null) {
            throw new BadRequestAlertException(MessageCode.MSG1036);
        } else if (request.getNewPassword() == null) {
            throw new BadRequestAlertException(MessageCode.MSG1037);
        } else if (request.getConfirmPassword() == null) {
            throw new BadRequestAlertException(MessageCode.MSG1038);
        } else if (!Validator.isPasswordRegex(request.getOldPassword()) ||
                !Validator.isPasswordRegex(request.getNewPassword()) ||
                !isStrongPassword(request.getNewPassword()) ||
                !Validator.isPasswordRegex(request.getConfirmPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1004);
        } else if (!passwordEncoder.matches(request.getOldPassword(), optionalUser.get().getPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1039);
        } else if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestAlertException(MessageCode.MSG1031);
        }

        List<PasswordHistory> historyPasswords = passwordHistoryRepository.findByEmail(request.getEmail());
        for (PasswordHistory historyPassword : historyPasswords) {
            if (passwordEncoder.matches(request.getNewPassword(), historyPassword.getPassword())) {
                throw new BadRequestAlertException(MessageCode.MSG1033);
            }
        }
        try{
            User newUser = optionalUser.get();
            newUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(newUser);
            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setEmail(request.getEmail());
            passwordHistory.setPassword(passwordEncoder.encode(request.getNewPassword()));
            passwordHistoryRepository.save(passwordHistory);
            ResponseObject response = new ResponseObject(Labels.getLabels(MessageCode.MSG1040.getKey()), 200, LocalDateTime.now(), newUser);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new BadRequestAlertException(MessageCode.MSG1014);
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") && password.matches(".*\\d.*");
    }
}
