package com.iceteasoftware.iam.configuration.security.jwt;


import com.iceteasoftware.iam.configuration.cache.CacheProperties;
import com.iceteasoftware.iam.configuration.message.LabelKey;
import com.iceteasoftware.iam.configuration.message.Labels;
import com.iceteasoftware.iam.configuration.security.AuthenticationProperties;
import com.iceteasoftware.iam.constant.SecurityConstants;
import com.iceteasoftware.iam.entity.User;
import com.iceteasoftware.iam.exception.handle.BadRequestAlertException;
import com.iceteasoftware.iam.exception.handle.UnauthorizedException;
import com.iceteasoftware.iam.repository.UserRepository;
import com.iceteasoftware.iam.service.impl.TokenService;
import com.iceteasoftware.iam.util.DateUtil;
import com.iceteasoftware.iam.util.GetterUtil;
import com.iceteasoftware.iam.util.Validator;
import com.iceteasoftware.iam.util.security.AbstractUserPrincipal;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTTokenProvider<T extends AbstractUserPrincipal> implements InitializingBean {
    private Key key;

    private final AuthenticationProperties properties;

    private final UserDetailsService userDetailsService;

    private final CacheProperties cacheProperties;

    private final TokenService tokenService;

    private JwtParser jwtParser;

    private Map<String, Integer> timeToLives;

    private final UserRepository userRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes;

        String secret = properties.getBase64Secret();

        if (Validator.isNull(secret)) {
            log.warn("Warning: the JWT key used is not Base64-encoded. "
                    + "We recommend using the `spring.security.authentication.jwt.base64-secret` key for optimum security.");

            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        } else {
            log.info("Using a Base64-encoded JWT secret key");

            keyBytes = Decoders.BASE64.decode(secret);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);

        // jwt parser
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

        // get timeToLives map
        this.timeToLives = cacheProperties.getTimeToLives();
    }
    public JWTAccessToken createAccessToken(String email, boolean rememberMe) {
        try {
            int duration = GetterUtil.getIntegerValue(rememberMe ? timeToLives.get(SecurityConstants.Cache.REMEMBER_ME_TOKEN)
                    : timeToLives.get(SecurityConstants.Cache.TOKEN), 0);

            JWTToken accessToken = createToken(email, duration, SecurityConstants.TokenType.ACCESS_TOKEN);

            JWTToken csrfToken = createToken(email, duration, SecurityConstants.TokenType.CSRF_TOKEN);

            JWTAccessToken jwtAccessToken = JWTAccessToken.builder()
                    .accessToken(accessToken)
                    .csrfToken(csrfToken)
                    .build();

            // invalidate token

                this.tokenService.invalidateToken(email);

                // save token
                if (rememberMe) {
                    this.tokenService.saveRememberMeToken(email, jwtAccessToken);
                } else {
                    this.tokenService.saveToken(email, jwtAccessToken);
                }


            return jwtAccessToken;
        } catch (UsernameNotFoundException e) {
            log.error(Labels.getLabels(LabelKey.ERROR_INVALID_EMAIL_OR_PASSWORD));

            throw new BadRequestAlertException(Labels.getLabels(LabelKey.ERROR_INVALID_EMAIL_OR_PASSWORD),
                    SecurityConstants.Header.TOKEN, LabelKey.ERROR_INVALID_EMAIL_OR_PASSWORD);
        }
    }
    public HttpCookie createHttpCookie(String key, String value, int duration) {

        return ResponseCookie.from(key, value).maxAge(duration).httpOnly(properties.isHttpOnly())
                .path(properties.getPath()).secure(properties.isEnableSsl()).sameSite(properties.getSameSite()).build();
    }

    public JWTToken createRefreshToken(String email) {
        JWTToken token = createRefreshToken(email, SecurityConstants.TokenType.REFRESH_TOKEN);

        // save refresh token
        this.tokenService.saveRefreshToken(email, token);

        return token;
    }
    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        String username = claims.getSubject();

        T principal = (T) userDetailsService.loadUserByUsername(username);

        if (Validator.isNull(principal)) {
            return null;
        }

//        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claims.get("role").toString()));
        log.info("claim: " + claims.toString() + "\n" + "principal: " + principal.toString());
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    }
    public boolean validateToken(String accessToken, String csrfToken) {
        String username;

        try {
            Claims claims = jwtParser.parseClaimsJws(accessToken).getBody();
            log.info("Token claims: {}", claims);
            log.info("Role in token: {}", claims.get("role"));

            username = claims.getSubject();

            if (Validator.isNull(username)) {
                log.error("username is empty");

                throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_INVALID_TOKEN),
                        SecurityConstants.Header.TOKEN, LabelKey.ERROR_INVALID_TOKEN);
            }

            JWTAccessToken token = this.tokenService.getToken(username);

            if (Validator.isNull(token)) {
                token = this.tokenService.getRememberMeToken(username);
            }

            if (Validator.isNull(token) || Validator.isNull(token.getAccessToken())
                    || Validator.isNull(token.getCsrfToken())) {
                return false;
            }

            return Validator.equals(accessToken, token.getAccessToken().getToken())
                    && Validator.equals(csrfToken, token.getCsrfToken().getToken());
        } catch (MalformedJwtException ex) {
            log.error("Access token malformed");

            throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_TOKEN_MALFORMED),
                    SecurityConstants.Header.TOKEN, LabelKey.ERROR_TOKEN_MALFORMED);

        } catch (ExpiredJwtException ex) {
            log.error("Access token has expired");

            throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_TOKEN_HAS_EXPIRED),
                    SecurityConstants.Header.TOKEN, LabelKey.ERROR_TOKEN_HAS_EXPIRED);

        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");

            throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_JWT_TOKEN_IS_UNSUPPORTED),
                    SecurityConstants.Header.TOKEN, LabelKey.ERROR_JWT_TOKEN_IS_UNSUPPORTED);

        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");

            throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_INVALID_TOKEN),
                    SecurityConstants.Header.TOKEN, Labels.getLabels(LabelKey.ERROR_INVALID_TOKEN));
        } catch (Exception e) {
            log.error("Invalid JWT signature.", e);

            throw new UnauthorizedException(Labels.getLabels(LabelKey.ERROR_INVALID_TOKEN),
                    SecurityConstants.Header.TOKEN, LabelKey.ERROR_INVALID_TOKEN);
        }
    }
    private JWTToken createToken(String email, int duration,String role, Map<String, Object> params) {
        Date expiration = DateUtil.getDateAfterSecond(new Date(), duration);
        String jwt = Jwts.builder()
                .setSubject(email)
                .addClaims(params)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .compact();

        return new JWTToken(jwt, duration, expiration,role);
    }

    private JWTToken createToken(String email, int duration, String tokenType) {
        Map<String, Object> params = new HashMap<>();
        params.put(SecurityConstants.Claim.TOKEN_TYPE, tokenType);
        Optional<User> user = userRepository.findByEmail(email);
//        params.put("role", user.get().getRole());
        params.put("role", "ROLE_" + user.get().getRole());
        return createToken(user.get().getEmail(), duration,user.get().getRole(), params);
    }
    private JWTToken createRefreshToken(String username, Map<String, Object> params) {
        String jwt = Jwts.builder()
                .setSubject(username)
                .addClaims(params)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .compact();
        return new JWTToken(jwt);
    }
    private JWTToken createRefreshToken(String username, String tokenType) {
        Map<String, Object> params = new HashMap<>();

        params.put(SecurityConstants.Claim.TOKEN_TYPE, tokenType);

        return createRefreshToken(username, params);
    }

}
