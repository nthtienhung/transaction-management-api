package com.transactionservice.configuration.auditing;

import com.iceteasoftware.common.util.ThreadLocalUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        String currentUser = ThreadLocalUtil.getCurrentUser();
        return Optional.ofNullable(currentUser).orElse("system").describeConstable(); // Hoặc một giá trị mặc định khác
    }
}

