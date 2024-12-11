package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public StatusRoleUserResponse getRoleAndStatusByUserId(String userId) {
        Optional<StatusRoleUserResponse> response = userRepository.findStatusRoleUser(userId);
        if(response.isPresent()) {
            StatusRoleUserResponse statusRoleUserResponse = new StatusRoleUserResponse();
            statusRoleUserResponse.setRole(response.get().getRole());
            statusRoleUserResponse.setStatus(response.get().getStatus());
            return statusRoleUserResponse;
        } else {
            throw new BadRequestAlertException(MessageCode.MSG1101);
        }
    }
}
