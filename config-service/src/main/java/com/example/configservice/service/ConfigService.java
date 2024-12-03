package com.example.configservice.service;


import com.example.configservice.dto.request.ConfigRequest;
import com.example.configservice.dto.response.ConfigResponse;
import com.example.configservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ConfigService {
    ConfigResponse addConfig(ConfigRequest request);
    ConfigResponse updateConfig(UUID configId, ConfigRequest request);
    Page<ConfigResponse> getConfigs(String group, String type, String configKey, Status status, Pageable pageable);
    ConfigResponse deleteConfig(UUID configId);
}
