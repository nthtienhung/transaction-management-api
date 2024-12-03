package com.iceteasoftware.notification.configuration.auditing;

import com.iceteasoftware.notification.dto.NotificationMessage;
import com.iceteasoftware.notification.util.ThreadLocalUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(ThreadLocalUtil.getCurrentUser());
    }


}

