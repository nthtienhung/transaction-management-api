package com.iceteasoftware.user.service.impl;

import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.repository.UserProfileRepository;
import com.iceteasoftware.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;

    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtSecret;

    @Override
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        // Lấy JWT từ header Authorization
        String jwt = getJwtFromHeader(request);

        if (jwt == null || jwt.isEmpty()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Token không tồn tại trong header Authorization",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        // Giải mã JWT và lấy email
        String email = this.extractEmailFromJwt(jwt);

        if (email == null) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Không thể giải mã email từ token",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        // Lấy thông tin người dùng từ email
        Optional<Profile> profile = this.getProfileByEmail(email);

        if (profile.isPresent()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Lấy thông tin profile thành công",
                    200,
                    LocalDateTime.now(),
                    profile.get()
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Không tìm thấy profile với email này",
                    404,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(404).body(response);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void createProfile(CreateProfileRequest request) {
        userProfileRepository.save(Profile.builder()
                        .userId(request.getUserId())
                        .dob(request.getDateOfBirth())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .address(request.getAddress())
                        .phone(request.getPhone())
                .build());
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userProfileRepository.findByPhone(phone).isPresent();
    }

    // Phương thức lấy JWT từ header Authorization
    private String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
       return header;
    }

    // Phương thức giải mã JWT và lấy email
    public String extractEmailFromJwt(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // Lấy thông tin profile từ email
    public Optional<Profile> getProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email);
    }
}
