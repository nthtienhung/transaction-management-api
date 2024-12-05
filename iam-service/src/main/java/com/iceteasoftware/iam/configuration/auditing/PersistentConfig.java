package com.iceteasoftware.iam.configuration.auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
/**
 * Cấu hình cho JPA Auditing trong ứng dụng.
 *
 * <p>Lớp này sử dụng {@link EnableJpaAuditing} để kích hoạt chức năng auditing
 * cho các thực thể JPA, cho phép ghi lại thông tin về người dùng thực hiện
 * các thao tác trên thực thể.</p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistentConfig {
    /**
     * Cung cấp {@link AuditorAware} để lấy thông tin về auditor hiện tại.
     *
     * <p>Phương thức này trả về một đối tượng {@link AuditorAwareConfig},
     * cho phép cung cấp thông tin về người dùng thực hiện thao tác trên thực thể.</p>
     *
     * @return một {@link AuditorAware} để cung cấp thông tin về auditor hiện tại
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareConfig();
    }
}
