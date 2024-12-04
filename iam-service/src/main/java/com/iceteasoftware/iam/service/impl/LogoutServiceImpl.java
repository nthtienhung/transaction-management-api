package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.configuration.message.LabelKey;
import com.iceteasoftware.iam.entity.UserLoginFailed;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.repository.UserLoginFailedRepository;
import com.iceteasoftware.iam.service.LogoutService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {
    private final UserLoginFailedRepository loginFailedRepository;
    @Override
    public void logout(HttpServletRequest request) {
        // Xóa thông tin trong SecurityContextHolder
        SecurityContextHolder.clearContext();

        // Hủy session hiện tại
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
