package com.iceteasoftware.iam.service.impl;


import com.iceteasoftware.common.message.Labels;
import com.iceteasoftware.iam.dto.request.changepassword.ChangePasswordRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.entity.PasswordHistory;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.PasswordHistoryRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.ChangePasswordService;
import com.iceteasoftware.common.util.Validator;
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

    /**
     * Changes the user's password based on the email provided in the request.
     * Validates the old password, ensures the new password meets strength criteria,
     * and checks against the user's password history to avoid reuse.
     *
     * @param request the {@link ChangePasswordRequest} containing email, old password, new password, and confirmation password.
     * @return a {@link ResponseEntity} containing a success message or an error message if validation fails.
     * @throws BadRequestAlertException if any validation rule is violated.
     */
    @Override
    public ResponseEntity<ResponseObject> changePasswordByEmail(ChangePasswordRequest request) {

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

        List<PasswordHistory> historyPasswords = passwordHistoryRepository.findTop5ByEmailOrderByCreatedAtDesc(request.getEmail());

        for (PasswordHistory historyPassword : historyPasswords) {
            if (passwordEncoder.matches(request.getNewPassword(), historyPassword.getPassword())) {
                throw new BadRequestAlertException(MessageCode.MSG1033);
            }
        }

        try {
            User newUser = optionalUser.get();
            newUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(newUser);

            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setEmail(request.getEmail());
            passwordHistory.setPassword(passwordEncoder.encode(request.getNewPassword()));
            passwordHistoryRepository.save(passwordHistory);

            ResponseObject response = new ResponseObject(
                    Labels.getLabels(MessageCode.MSG1040.getKey()),
                    200,
                    LocalDateTime.now(),
                    newUser
            );
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            throw new BadRequestAlertException(MessageCode.MSG1014);
        }
    }

    /**
     * Validates if the given password meets the strength criteria:
     * - Minimum 8 characters
     * - Maximum 20 characters
     * - Contains at least one uppercase letter
     * - Contains at least one lowercase letter
     * - Contains at least one digit
     *
     * @param password the password to validate.
     * @return {@code true} if the password meets the criteria, {@code false} otherwise.
     */
    private boolean isStrongPassword(String password) {
        return password.length() >= 8 &&
                password.length() <= 20 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*");
    }
}
