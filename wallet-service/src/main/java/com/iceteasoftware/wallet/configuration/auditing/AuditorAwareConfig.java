package com.iceteasoftware.wallet.configuration.auditing;

import com.iceteasoftware.wallet.util.ThreadLocalUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(ThreadLocalUtil.getCurrentUser());
    }
}

