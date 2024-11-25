package com.example.iamservice.service.impl;

import com.example.iamservice.configuration.security.jwt.JWTAccessToken;
import com.example.iamservice.configuration.security.jwt.JWTToken;
import com.example.iamservice.configuration.security.jwt.JWTTokenProvider;
import com.example.iamservice.constant.Constants;
import com.example.iamservice.constant.SecurityConstants;
import com.example.iamservice.dto.request.login.LoginRequest;
import com.example.iamservice.dto.response.common.ResponseObject;
import com.example.iamservice.dto.response.login.TokenResponse;

import com.example.iamservice.entity.User;
import com.example.iamservice.entity.UserLoginFailed;
import com.example.iamservice.entity.UserProperties;
import com.example.iamservice.enums.MessageCode;
import com.example.iamservice.enums.Status;
import com.example.iamservice.exception.handler.BadRequestAlertException;
import com.example.iamservice.repository.UserLoginFailedRepository;
import com.example.iamservice.repository.UserRepository;
import com.example.iamservice.service.LoginService;
import com.example.iamservice.util.GetterUtil;
import com.example.iamservice.util.Validator;
import com.example.iamservice.util.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final UserLoginFailedRepository userLoginFailedRepository;
    private final PasswordEncoder passwordEncoder;

    @Getter
    private final UserProperties userProperties;
    private final JWTTokenProvider<UserPrincipal> jwtTokenProvider;

    @Override
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   LoginRequest loginRequest) {
        String email = GetterUtil.getString(loginRequest.getEmail());
        String password = GetterUtil.getString(loginRequest.getPassword());

        // Check email có để trống k
        if (Validator.isNull(email)) {
            throw new BadRequestAlertException(MessageCode.MSG1003);
        } else if (com.example.iamservice.validator.Validator.isEmail(loginRequest.getEmail())
                || loginRequest.getEmail().length() > Constants.DEFAULT_EMAIL_LENGTH_MAX) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        } else if (Validator.isNull(password)) {
            // Check password có để trống k
            throw new BadRequestAlertException(MessageCode.MSG1001);
        }

        //Lấy thông tin user
        Optional<User> user = userRepository.findByEmail(email);

        // Check tài khoản có bị block k
        if (Validator.equals(Status.BLOCK.name(), user.get().getStatus())) {
            throw new BadRequestAlertException(MessageCode.MSG1006);
        } else if (Validator.equals(Status.INACTIVE.name(), user.get().getStatus())) {
            // Check tài khoản đã được active chưa
            throw new BadRequestAlertException(MessageCode.MSG1008);
        }

        UserLoginFailed userLoginFailed = this.userLoginFailedRepository.findByUserId(user.get().getUserId());

        // kiểm tra xem có đang bị tạm khóa không
        if (Validator.isNotNull(userLoginFailed) && Validator.isNotNull(userLoginFailed.getUnlockTime())
                && userLoginFailed.getUnlockTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestAlertException(MessageCode.MSG1007);
        } else if (!passwordEncoder.matches(password, user.get().getPassword())) {
            // Check password
            this.updateLoginAttempts(userLoginFailed, false);

            throw new BadRequestAlertException(MessageCode.MSG1005);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        boolean rememberMe = loginRequest.getRememberMe() != null && loginRequest.getRememberMe();

        JWTAccessToken accessToken = this.jwtTokenProvider.createAccessToken(email, rememberMe);

        JWTToken refreshToken = this.jwtTokenProvider.createRefreshToken(email);

        TokenResponse tokenResponse = TokenResponse.builder()//
                .type(SecurityConstants.Header.BEARER_START.trim())
                .csrfToken(accessToken.getCsrfToken().getToken())
                .refreshToken(refreshToken.getToken())
                .csrfTokenDuration(accessToken.getCsrfToken().getDuration())
                .csrfExpiresAt(accessToken.getCsrfToken().getExpiredTime())
                .build();

        // clear login fail attempt
        this.updateLoginAttempts(userLoginFailed, true);

        HttpCookie accessCookie = this.jwtTokenProvider.createHttpCookie(SecurityConstants.Cookie.ACCESS_TOKEN,
                accessToken.getAccessToken().getToken(), accessToken.getAccessToken().getDuration());

        HttpCookie rememberMeCookie = this.jwtTokenProvider.createHttpCookie(SecurityConstants.Cookie.REMEMBER_ME,
                String.valueOf(rememberMe), accessToken.getAccessToken().getDuration());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        httpHeaders.add(HttpHeaders.SET_COOKIE, rememberMeCookie.toString());

        ResponseObject<TokenResponse> data = new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS,
                HttpStatus.OK.value(), LocalDateTime.now(), tokenResponse);

        return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
    }

    private void updateLoginAttempts(UserLoginFailed userLoginFailed, boolean success) {
        if (success) {
            // clear login failed attempts
            userLoginFailed.setUnlockTime(null);
            userLoginFailed.setLoginFailedAttempts(0);
        } else {
            int loginFailedAttempts = userLoginFailed.getLoginFailedAttempts() + 1;

            userLoginFailed.setLoginFailedAttempts(loginFailedAttempts);

            if (this.userProperties.getLoginAttempts() > 0
                    && loginFailedAttempts >= this.userProperties.getLoginAttempts()) {
                userLoginFailed.setUnlockTime(
                        LocalDateTime.now().plusMinutes(this.userProperties.getLoginFailedLockDuration()));
            }
        }

        this.userLoginFailedRepository.save_(userLoginFailed);
    }

    private User findByEmail(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1005);
        }

        return user.orElse(null);
    }
}


