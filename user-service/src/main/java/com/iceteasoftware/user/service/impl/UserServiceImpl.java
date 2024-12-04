package com.iceteasoftware.user.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iceteasoftware.user.constant.Constants;
import com.iceteasoftware.user.constant.KafkaTopicConstants;
import com.iceteasoftware.user.dto.request.CreateProfileRequest;
import com.iceteasoftware.user.dto.response.common.ResponseObject;
import com.iceteasoftware.user.entity.Profile;
import com.iceteasoftware.user.repository.UserProfileRepository;
import com.iceteasoftware.user.service.UserService;
import com.iceteasoftware.user.util.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                    "Token is missing in the Authorization header",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        String email = extractEmailFromJwt(jwt);

        if (email == null) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Unable to decode email from token",
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
                        "Successfully retrieved profile",
                        200,
                        LocalDateTime.now(),
                        profile.get()
                );
                return ResponseEntity.ok(response);
            } else {
                ResponseObject<Profile> response = new ResponseObject<>(
                        "Profile not found for the provided email",
                        404,
                        LocalDateTime.now(),
                        null
                );
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching profile: ", e);
            ResponseObject<Profile> response = new ResponseObject<>(
                    "An error occurred while fetching the profile",
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
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
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
                    "Token is missing in the Authorization header",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        String email = extractEmailFromJwt(jwt);

        if (email == null) {
            ResponseObject<Profile> response = new ResponseObject<>(
                    "Unable to decode email from token",
                    401,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(401).body(response);
        }

        try {
            Optional<Profile> optionalProfile = getProfileByEmail(email);

            if (optionalProfile.isEmpty()) {
                ResponseObject<Profile> response = new ResponseObject<>(
                        "Profile not found for the provided email",
                        404,
                        LocalDateTime.now(),
                        null
                );
                return ResponseEntity.status(404).body(response);
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
                    "Profile updated successfully",
                    200,
                    LocalDateTime.now(),
                    existingProfile
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error occurred while updating profile: ", e);
            ResponseObject<Profile> response = new ResponseObject<>(
                    "An error occurred while updating the profile",
                    500,
                    LocalDateTime.now(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}
