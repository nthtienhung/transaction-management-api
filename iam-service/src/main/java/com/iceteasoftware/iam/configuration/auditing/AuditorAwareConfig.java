package com.iceteasoftware.iam.configuration.auditing;

import com.iceteasoftware.iam.configuration.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails.getUserId());
        } else {
            return Optional.of("USER_SIGN_UP");
        }
    }
}

