package com.iceteasoftware.iam.configuration.auditing;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
/**
 * Cấu hình cho AuditorAware để cung cấp thông tin về auditor hiện tại.
 *
 * <p>Lớp này implements {@link AuditorAware} và cung cấp thông tin về người dùng
 * hiện tại (auditor) cho các thực thể được audit.</p>
 */
public class AuditorAwareConfig implements AuditorAware<Integer> {
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
    public Optional<Integer> getCurrentAuditor() {
        return Optional.of(1);
    }
}

