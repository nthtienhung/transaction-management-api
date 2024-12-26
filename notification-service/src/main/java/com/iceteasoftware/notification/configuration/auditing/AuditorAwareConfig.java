package com.iceteasoftware.notification.configuration.auditing;

import com.iceteasoftware.common.util.ThreadLocalUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(ThreadLocalUtil.getCurrentUser());
    }


}

