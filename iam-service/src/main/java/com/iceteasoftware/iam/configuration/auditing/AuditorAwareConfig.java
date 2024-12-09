package com.iceteasoftware.iam.configuration.auditing;

import com.iceteasoftware.iam.configuration.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Cấu hình cho AuditorAware để cung cấp thông tin về auditor hiện tại.
 *
 * <p>Lớp này implements {@link AuditorAware} và cung cấp thông tin về người dùng
 * hiện tại (auditor) cho các thực thể được audit.</p>
 */
public class AuditorAwareConfig implements AuditorAware<String> {
    /**
     * Trả về thông tin về auditor hiện tại.
     *
     * <p>Trong trường hợp này, luôn trả về một số cố định (1),
     * nhưng trong thực tế, bạn có thể thay đổi để trả về ID của người dùng đang
     * thực hiện thao tác.</p>
     *
     * @return một {@link Optional} chứa ID của auditor hiện tại,
     *         trong trường hợp này là 1.
     */
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

