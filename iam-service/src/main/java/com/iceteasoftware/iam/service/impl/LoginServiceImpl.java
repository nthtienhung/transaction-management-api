package com.iceteasoftware.iam.service.impl;

import com.iceteasoftware.iam.configuration.message.LabelKey;
import com.iceteasoftware.iam.configuration.security.jwt.JWTAccessToken;
import com.iceteasoftware.iam.configuration.security.jwt.JWTToken;
import com.iceteasoftware.iam.configuration.security.jwt.JWTTokenProvider;
import com.iceteasoftware.iam.validator.Validator;
import com.iceteasoftware.iam.constant.Constants;
import com.iceteasoftware.iam.constant.SecurityConstants;
import com.iceteasoftware.iam.dto.request.login.LoginRequest;
import com.iceteasoftware.iam.dto.response.common.ResponseObject;
import com.iceteasoftware.iam.dto.response.login.TokenResponse;

import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.entity.UserLoginFailed;
import com.iceteasoftware.iam.entity.UserProperties;
import com.iceteasoftware.iam.enums.MessageCode;
import com.iceteasoftware.iam.enums.Status;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.repository.UserLoginFailedRepository;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.LoginService;
import com.iceteasoftware.iam.util.GetterUtil;
import com.iceteasoftware.iam.util.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final UserLoginFailedRepository userLoginFailedRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private static final String NOT_VERIFIED = "NOT_VERIFIED";
    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtSecret;
    @Getter
    private final UserProperties userProperties;
    private final JWTTokenProvider<UserPrincipal> jwtTokenProvider;

    @Override
    public ResponseEntity<ResponseObject<TokenResponse>> authorize(HttpServletRequest request,
                                                                   LoginRequest loginRequest) {
        String email = GetterUtil.getString(loginRequest.getEmail());
        String password = GetterUtil.getString(loginRequest.getPassword());

        // Check email có để trống k
        if (com.iceteasoftware.iam.util.Validator.isNull(email)) {
            throw new BadRequestAlertException(MessageCode.MSG1003);
        } else if (Validator.isEmail(loginRequest.getEmail())
                || loginRequest.getEmail().length() > Constants.DEFAULT_EMAIL_LENGTH_MAX) {
            throw new BadRequestAlertException(MessageCode.MSG1002);
        } else if (com.iceteasoftware.iam.util.Validator.isNull(password)) {
            // Check password có để trống k
            throw new BadRequestAlertException(MessageCode.MSG1001);
        }

        //Lấy thông tin user
        Optional<User> user = userRepository.findByEmail(email);
        if(!loginRequest.getRole().equals(user.get().getRole())) {
            throw new BadRequestAlertException(MessageCode.MSG1049);
        }
        // Check tài khoản có bị block k
        if (!user.get().getStatus()) {
            throw new BadRequestAlertException(MessageCode.MSG1006);
        } else if (user.get().getIsVerified().equals("NOT_VERIFIED")) {
            // Check tài khoản đã được active chưa
            throw new BadRequestAlertException(MessageCode.MSG1008);
        }

        UserLoginFailed userLoginFailed = this.userLoginFailedRepository.findByUserId(user.get().getUserId());

        // kiểm tra xem có đang bị tạm khóa không
        if (com.iceteasoftware.iam.util.Validator.isNotNull(userLoginFailed) && com.iceteasoftware.iam.util.Validator.isNotNull(userLoginFailed.getUnlockTime())
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
     @Override
    public Optional<User> getUser(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadRequestAlertException(MessageCode.MSG1005);
        }
        return user;
    }

    @Override
    public ResponseEntity<ResponseObject<TokenResponse>> refreshToken(HttpServletRequest request) {
        String refreshTokenOld = resolveToken(request);
        if (refreshTokenOld == null) {
            throw new BadRequestAlertException(MessageCode.MSG1057);
        }
        String email = getSubjectFromToken(refreshTokenOld);

        try {
            JWTToken refreshTokenUserCheck = getTokenDetails(refreshTokenOld);
            LocalDate now = LocalDate.now();
            Date date = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
            if(refreshTokenUserCheck.getExpiredTime().before(date)) {
              throw new BadRequestAlertException(MessageCode.MSG1057);
            }else {
                JWTAccessToken accessToken = this.jwtTokenProvider.createAccessToken(email, false);
                JWTToken refreshToken = this.jwtTokenProvider.createRefreshToken(email);
                // Chuẩn bị TokenResponse để trả về cho client
                TokenResponse tokenResponse = TokenResponse.builder()//
                        .type(SecurityConstants.Header.BEARER_START.trim())
                        .csrfToken(accessToken.getCsrfToken().getToken())
                        .refreshToken(refreshToken.getToken())
                        .csrfTokenDuration(accessToken.getCsrfToken().getDuration())
                        .csrfExpiresAt(accessToken.getCsrfToken().getExpiredTime())
                        .build();

                // Nếu sử dụng cookie, tạo cookie chứa các token mới
                HttpCookie accessCookie = this.jwtTokenProvider.createHttpCookie(SecurityConstants.Cookie.ACCESS_TOKEN,
                        accessToken.getAccessToken().getToken(), accessToken.getAccessToken().getDuration());

                HttpCookie rememberMeCookie = this.jwtTokenProvider.createHttpCookie(SecurityConstants.Cookie.REMEMBER_ME,
                        String.valueOf(false), accessToken.getAccessToken().getDuration());

                // Thêm cookie vào header
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
                httpHeaders.add(HttpHeaders.SET_COOKIE, rememberMeCookie.toString());

                // Trả về ResponseEntity chứa token mới
                ResponseObject<TokenResponse> data = new ResponseObject<>(Constants.DEFAULT_MESSAGE_SUCCESS,
                        HttpStatus.OK.value(), LocalDateTime.now(), tokenResponse);

                return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
            }
        }catch (Exception e) {
            throw new BadRequestAlertException(MessageCode.MSG1057);
        }

    }
    public Claims parseToken(String refreshToken) {
        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(refreshToken);

        return jwsClaims.getBody(); // Returns the claims (payload) of the token
    }
    public JWTToken getTokenDetails(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        String role = claims.get("role", String.class);
        Date expiration = claims.getExpiration();
        return new JWTToken(role,expiration);
    }
    public String resolveToken(HttpServletRequest request) {
        // Lấy token từ Authorization header
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null ) {
            // Trả về token mà không có tiền tố "Bearer "
            return bearerToken;
        }

        // Nếu không tìm thấy trong header, kiểm tra cookie (nếu sử dụng)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SecurityConstants.Cookie.REFRESH_TOKEN)) {
                    return cookie.getValue();
                }
            }
        }

        // Nếu không tìm thấy token, trả về null
        return null;
    }

    public String getSubjectFromToken(String token) {
        try {
            // Parse token để lấy thông tin claims
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret) // secretKey là khóa bí mật để ký và xác thực token
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Trả về thông tin subject (thường là email hoặc userId)
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            // Token không hợp lệ hoặc không thể parse
            throw new BadRequestAlertException(MessageCode.MSG1050);
        }
    }
}


