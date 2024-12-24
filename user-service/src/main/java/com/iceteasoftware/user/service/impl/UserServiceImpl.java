package com.iceteasoftware.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceteasoftware.user.client.IamClient;
import com.iceteasoftware.user.constant.KafkaTopicConstants;
import com.iceteasoftware.user.configuration.message.Labels;
import com.iceteasoftware.user.dto.UserProfileResponse;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.StatusRoleUserResponse;
import com.iceteasoftware.user.dto.response.UserResponse;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.dto.response.profile.FullNameResponse;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.entity.User;
import com.iceteasoftware.user.enums.MessageCode;
import com.iceteasoftware.user.enums.Status;
import com.iceteasoftware.user.exception.handler.BadRequestAlertException;
import com.iceteasoftware.user.repository.UserProfileRepository;
import com.iceteasoftware.user.repository.UserRepository;
import com.iceteasoftware.user.service.UserService;
import com.iceteasoftware.user.util.ThreadLocalUtil;
import io.jsonwebtoken.*;
import com.iceteasoftware.user.util.Validator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;
    private final IamClient iamClient;
    @Value("${security.authentication.jwt.base64-secret}")
    private String jwtSecret;

    /**
     * Retrieves a user's profile based on the JWT provided in the request.
     *
     * @param request the HTTP request containing the JWT in the Authorization header.
     * @return a {@link ResponseEntity} containing the profile if found, or an error response.
     */
    @Override
    public ResponseEntity<ResponseObject<Profile>> getProfile(HttpServletRequest request) {
        String jwt = getJwtFromHeader(request);

        if (jwt == null || jwt.isEmpty()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1056.getKey()),
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        String email = extractEmailFromJwt(jwt);

        if (email == null) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1057.getKey()),
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        try {
            Optional<Profile> profile = getProfileByEmail(email);

            if (profile.isPresent()) {
                ResponseObject<Profile> response = new ResponseObject<>(
                        Labels.getLabels(MessageCode.MSG1058.getKey()),
                        200,
                        LocalDateTime.now(),
                        profile.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ResponseObject<Profile> response = new ResponseObject<>(
                        Labels.getLabels(MessageCode.MSG1059.getKey()),
                        404,
                        LocalDateTime.now(),
                        null
                );
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1060.getKey()),
                    500,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Listens for profile creation messages from a Kafka topic and creates a profile for the specified user.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *     <li>Parses the incoming Kafka message to extract user profile details.</li>
     *     <li>Sets the user ID in the current thread context for tracking purposes.</li>
     *     <li>Saves a new profile entry with the provided details to the database.</li>
     *     <li>Removes the user ID from the thread context after processing is complete.</li>
     * </ul>
     *
     * @param createProfileMessage the JSON message received from the Kafka topic, containing profile details.
     * @throws JsonProcessingException if the message cannot be parsed as JSON.
     */
    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopicConstants.DEFAULT_KAFKA_TOPIC_CREATE_PROFILE, groupId = "create-group")
    public void createProfile(String createProfileMessage) throws JsonProcessingException {
        log.info("Received message: {}", createProfileMessage);
        JsonNode jsonNode = objectMapper.readTree(createProfileMessage);
        String userId = jsonNode.get("userId").asText();
        String firstName = jsonNode.get("firstName").asText();
        String lastName = jsonNode.get("lastName").asText();
        String email = jsonNode.get("email").asText();
        LocalDate dob = LocalDate.parse(jsonNode.get("dob").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String address = jsonNode.get("address").asText();
        String phone = jsonNode.get("phone").asText();

        ThreadLocalUtil.setCurrentUser(userId);

        userProfileRepository.save(Profile.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .address(address)
                .phone(phone)
                .dob(dob)
                .userId(userId)
                .build());

        ThreadLocalUtil.remove();

    }

    /**
     * Checks if a phone number already exists in the database.
     *
     * @param phone the phone number to check.
     * @return {@code true} if the phone number exists, otherwise {@code false}.
     */
    @Override
    public boolean isPhoneExists(String phone) {
        return userProfileRepository.findByPhone(phone).isPresent();
    }

    @Override
    public String getRole(HttpServletRequest request) {
        String jwt = getJwtFromHeader(request);

        if (jwt == null || jwt.isEmpty()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Token không tồn tại trong header Authorization",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return null;
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
            return null;
        }
        Optional<User> userGetRole = userRepository.findByEmail(email);
        return userGetRole.get().getRole();
    }

    // Phương thức lấy JWT từ header Authorization

    /**
     * Extracts the JWT from the Authorization header of the request.
     *
     * @param request the HTTP request.
     * @return the JWT if present, otherwise {@code null}.
     */
    private String getJwtFromHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        return header;
    }

    /**
     * Decodes the JWT and extracts the email of the user.
     *
     * @param jwt the JWT string.
     * @return the email extracted from the token, or {@code null} if decoding fails.
     */
    private String extractEmailFromJwt(String jwt) {
        try {
            // Remove "Bearer " prefix if present
            jwt = removeBearerPrefix(jwt);

            // Validate the JWT
            if (jwt == null || jwt.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT string is null or empty");
            }

            // Parse claims
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.getSubject();
        } catch (IllegalArgumentException e) {
            System.err.println("JWT string is null or empty: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Unsupported JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
        }
        return null;
    }
    public static String removeBearerPrefix(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim(); // Remove "Bearer " (7 characters) and trim spaces
        }
        return token; // Return the original token if no "Bearer " prefix is found
    }
    /**
     * Retrieves a user's profile based on their email address.
     *
     * @param email the email address to search for.
     * @return an {@link Optional} containing the user's profile if found, or {@code Optional.empty()}.
     */
    private Optional<Profile> getProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email);
    }

    /**
     * Updates a user's profile based on the provided request and JWT in the Authorization header.
     *
     * @param request       the HTTP request containing the JWT.
     * @param updateRequest the data to update the user's profile.
     * @return a {@link ResponseEntity} containing the updated profile if successful, or an error response.
     */
    @Override
    public ResponseEntity<ResponseObject<Profile>> updateProfile(
            HttpServletRequest request,
            CreateProfileRequest updateRequest) {

        String jwt = getJwtFromHeader(request);

        if (jwt == null || jwt.isEmpty()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1056.getKey()),
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        String email = extractEmailFromJwt(jwt);

        if (email == null) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1057.getKey()),
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        // try {
            Optional<Profile> optionalProfile = getProfileByEmail(email);

            if(optionalProfile.isEmpty()) {
                ResponseObject<Profile> response = new ResponseObject<>(
                        Labels.getLabels(MessageCode.MSG1059.getKey()),
                        404,
                        LocalDateTime.now(),
                        null
                );
                return ResponseEntity.status(404).body(response);
            } else if(Validator.isBlankOrEmpty(updateRequest.getFirstName())) {
                throw new BadRequestAlertException(MessageCode.MSG1053);
            } else if(Validator.isBlankOrEmpty(updateRequest.getLastName())) {
                throw new BadRequestAlertException(MessageCode.MSG1054);
            } else if(Validator.isBlankOrEmpty(updateRequest.getPhone())) {
                throw new BadRequestAlertException(MessageCode.MSG1045);
            } else if(!Validator.isVNPhoneNumber(updateRequest.getPhone())){
                throw new BadRequestAlertException(MessageCode.MSG1044);
            }else if(updateRequest.getDob().isAfter(LocalDate.now())){
                throw new BadRequestAlertException(MessageCode.MSG1019);
            }

            Profile existingProfile = optionalProfile.get();

            // Update profile details
            existingProfile.setFirstName(updateRequest.getFirstName());
            existingProfile.setLastName(updateRequest.getLastName());
            existingProfile.setAddress(updateRequest.getAddress());
            existingProfile.setPhone(updateRequest.getPhone());
            existingProfile.setDob(updateRequest.getDob());

            // Save the updated profile
            userProfileRepository.save(existingProfile);

            ResponseObject<Profile> response = new ResponseObject<>(
                    Labels.getLabels(MessageCode.MSG1061.getKey()),
                    200,
                    LocalDateTime.now(),
                    existingProfile
            );

            return ResponseEntity.ok(response);
        // } catch (Exception e) {
        //     ResponseObject<Profile> response = new ResponseObject<>(
        //             Labels.getLabels(MessageCode.MSG1060.getKey()),
        //             500,
        //             LocalDateTime.now(),
        //             null
        //     );
        //     return ResponseEntity.status(500).body(response);
        // }
    }

    @Override
    public ResponseEntity<User> findUser(HttpServletRequest request) {
        String jwt = getJwtFromHeader(request);

        if (jwt == null || jwt.isEmpty()) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Token không tồn tại trong header Authorization",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return null;
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
            return null;
        }
        Optional<User> user = userRepository.findByEmail(email);
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    /**
     * Retrieves a paginated list of user profiles with optional search functionality and excludes profiles with the role "Admin".
     *
     * <p>Steps:
     * <ol>
     *   <li>Fetch all profiles using the provided {@link Pageable} parameter.</li>
     *   <li>If a search term is provided, filter profiles by first name or last name using case-insensitive matching.</li>
     *   <li>Filter out profiles with the role "Admin" by querying the IAM client for each profile's role.</li>
     *   <li>Map each profile to a {@link UserProfileResponse} object, including the role and status from the IAM client.</li>
     *   <li>Return a {@link Page} of {@link UserProfileResponse} objects.</li>
     * </ol>
     *
     * @param pageable   the {@link Pageable} object specifying the pagination information (page number, size, and sort order).
     * @param searchTerm an optional search term to filter profiles by first name or last name (case-insensitive).
     * @return a {@link Page} of {@link UserProfileResponse} objects containing filtered and mapped user profiles.
     */
    @Override
    public Page<UserProfileResponse> getAllUserProfile(Pageable pageable, String searchTerm) {
        // Use Spring's Pageable directly
        Page<Profile> profilePage = userProfileRepository.findAll(pageable);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            profilePage = userProfileRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                searchTerm, 
                searchTerm, 
                pageable);
        } else {
            profilePage = userProfileRepository.findAll(pageable);
        }

        //return profiles, fitler out profiles with role=admin
        return new PageImpl<>(
            profilePage.getContent().stream()
                .filter(profile -> {
                    StatusRoleUserResponse statusRoleUserResponse = iamClient.getRoleStatus(profile.getUserId());
                    return !"Admin".equalsIgnoreCase(statusRoleUserResponse.getRole());
                })
                .map(profile -> {
                    StatusRoleUserResponse statusRoleUserResponse = iamClient.getRoleStatus(profile.getUserId());
                    return UserProfileResponse.builder()
                            .userId(profile.getUserId())
                            .firstName(profile.getFirstName())
                            .lastName(profile.getLastName())
                            .email(profile.getEmail())
                            .phone(profile.getPhone())
                            .dob(profile.getDob())
                            .address(profile.getAddress())
                            .role(statusRoleUserResponse.getRole())
                            .isVerified(statusRoleUserResponse.getIsVerified())
                            .status(statusRoleUserResponse.getStatus())
                            .build();
                })
                .collect(Collectors.toList()),
            pageable,
            profilePage.getTotalElements()
        );
    }

    public UserResponse getUserById(String userId) {
        System.out.println("User ID: " + userId);
        Profile profile = userProfileRepository.findByUserId(userId).orElse(null);

        System.out.println("Profile: " + profile);

        return UserResponse.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .address(profile.getAddress())
                .build();
    }

    @Override
    public Boolean isEmailExists(String email) {
        Optional<Profile> profile = userProfileRepository.findByEmail(email);
        return profile.isEmpty();
    }

    public FullNameResponse getFullNameByUserId(String userId) {
        Optional<FullNameResponse> user = userProfileRepository.findUserById(userId);

        if (user.isPresent()) {
            FullNameResponse response = new FullNameResponse();
            response.setFirstName(user.get().getFirstName());
            response.setLastName(user.get().getLastName());
            return response;
        } else {
            return null;
        }
    }
}
