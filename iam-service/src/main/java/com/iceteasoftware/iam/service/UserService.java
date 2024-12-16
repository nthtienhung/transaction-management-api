package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.iam.enums.Status;

public interface UserService {

    StatusRoleUserResponse getRoleAndStatusByUserId(String userId);
    void updateUserStatus(String userId, Status status);
}
