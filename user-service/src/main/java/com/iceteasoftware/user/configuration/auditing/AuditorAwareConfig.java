package com.iceteasoftware.user.configuration.auditing;

import com.iceteasoftware.user.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        log.info("Current user:{}", ThreadLocalUtil.getCurrentUser());
        return Optional.ofNullable(ThreadLocalUtil.getCurrentUser());
    }
}

