package com.iceteasoftware.iam.service;

import com.iceteasoftware.iam.dto.response.StatusRoleUserResponse;

public interface UserService {

    StatusRoleUserResponse getRoleAndStatusByUserId(String userId);

}
