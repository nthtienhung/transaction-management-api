package com.iceteasoftware.config.service.impl;


import com.iceteasoftware.config.dto.request.ConfigRequest;
import com.iceteasoftware.config.dto.response.ConfigResponse;
import com.iceteasoftware.config.entity.Config;
import com.iceteasoftware.config.enums.MessageCode;
import com.iceteasoftware.config.enums.Status;
import com.iceteasoftware.config.exception.handler.BadRequestAlertException;
import com.iceteasoftware.config.exception.handler.ConflictAlertException;
import com.iceteasoftware.config.exception.handler.NotFoundAlertException;
import com.iceteasoftware.config.repository.ConfigRepository;
import com.iceteasoftware.config.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Config Service Implementation
 *
 * @author minhquang
 * @version 1.0
 * @since 2024-22-11
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigRepository configRepository;

    /**
     * Method to add a new Configure
     *
     * @param request
     * @return
     */
    @Override
    public ConfigResponse addConfig(ConfigRequest request) {
        validateRequest(request);

        // Kiểm tra config có tồn tại với 4 giá trị (group, type, key, value)
        Optional<Config> existingWithAllValues = configRepository.findByGroupAndTypeAndKeyAndValue(
                request.getGroup(),
                request.getType(),
                request.getConfigKey(),
                request.getConfigValue()
        );

        if (existingWithAllValues.isPresent()) {
            Config existingConfig = existingWithAllValues.get();
            if (existingConfig.getStatus() == Status.ACTIVE) {
                System.out.println("exist");
                throw new ConflictAlertException(MessageCode.MSG2111);
            }
            // Nếu inactive thì chuyển thành active
            existingConfig.setStatus(Status.ACTIVE);
            existingConfig.setUpdate_at(LocalDateTime.now());
            existingConfig.setUpdated_by("ADMIN");
            // Kiểm tra config có tồn tại với 3 giá trị đầu (group, type, key)
            Optional<Config> existingWithThreeValues = configRepository.findByGroupAndTypeAndKeyAndActiveStatus(
                    request.getGroup(),
                    request.getType(),
                    request.getConfigKey()
            );
            if (existingWithThreeValues.isPresent()) {
                // Set status của record cũ thành inactive
                Config oldConfig = existingWithThreeValues.get();
                System.out.println(oldConfig.toString());
                oldConfig.setStatus(Status.INACTIVE);
                oldConfig.setUpdate_at(LocalDateTime.now());
                oldConfig.setUpdated_by("ADMIN");
                configRepository.save(oldConfig);
            }
            return mapToResponse(configRepository.save(existingConfig));
        }

        // Kiểm tra config có tồn tại với 3 giá trị đầu (group, type, key)
        Optional<Config> existingWithThreeValues = configRepository.findByGroupAndTypeAndKeyAndActiveStatus(
                request.getGroup(),
                request.getType(),
                request.getConfigKey()
        );

        if (existingWithThreeValues.isPresent()) {
            // Set status của record cũ thành inactive
            Config oldConfig = existingWithThreeValues.get();
            System.out.println(oldConfig.toString());
            oldConfig.setStatus(Status.INACTIVE);
            oldConfig.setUpdate_at(LocalDateTime.now());
            oldConfig.setUpdated_by("ADMIN");
            configRepository.save(oldConfig);
        }

        // Tạo config mới
        Config newConfig = Config.builder()
                .group(request.getGroup())
                .type(request.getType())
                .key(request.getConfigKey())
                .value(request.getConfigValue().trim())
                .status(Status.ACTIVE)
                .create_at(LocalDateTime.now())
                .created_by("ADMIN")
                .update_at(LocalDateTime.now())
                .updated_by("ADMIN")
                .build();

        return mapToResponse(configRepository.save(newConfig));
    }

    /**
     * Method to update a Configure
     *
     * @param configId
     * @param request
     * @return
     */
    @Override
    public ConfigResponse updateConfig(UUID configId, ConfigRequest request) {
        validateRequest(request);

        // Tìm config cần update
        Config existingConfig = configRepository.findById(configId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG2109));

        // Kiểm tra nếu không có gì thay đổi
        if (existingConfig.getGroup().equals(request.getGroup()) &&
                existingConfig.getType().equals(request.getType()) &&
                existingConfig.getKey().equals(request.getConfigKey()) &&
                existingConfig.getValue().equals(request.getConfigValue())) {

            if (existingConfig.getStatus() != request.getStatus()) {

                if(request.getStatus() == Status.ACTIVE){

                    // Kiểm tra config có tồn tại với 3 giá trị đầu (group, type, key)
                    Optional<Config> existingWithThreeValues = configRepository.findByGroupAndTypeAndKeyAndActiveStatus(
                            request.getGroup(),
                            request.getType(),
                            request.getConfigKey()
                    );

                    if (existingWithThreeValues.isPresent()){
                        Config oldConfig = existingWithThreeValues.get();
                        oldConfig.setStatus(Status.INACTIVE);
                        oldConfig.setUpdate_at(LocalDateTime.now());
                        oldConfig.setUpdated_by("ADMIN");
                        configRepository.save(oldConfig);

                        existingConfig.setStatus(request.getStatus());
                        existingConfig.setUpdate_at(LocalDateTime.now());
                        existingConfig.setUpdated_by("ADMIN");
                        configRepository.save(existingConfig);
                        return mapToResponse(existingConfig);
                    }

                    existingConfig.setStatus(request.getStatus());
                    existingConfig.setUpdate_at(LocalDateTime.now());
                    existingConfig.setUpdated_by("ADMIN");
                    configRepository.save(existingConfig);
                    return mapToResponse(existingConfig);
                }
                else {
                    existingConfig.setStatus(request.getStatus());
                    existingConfig.setUpdate_at(LocalDateTime.now());
                    existingConfig.setUpdated_by("ADMIN");
                    configRepository.save(existingConfig);
                    return mapToResponse(existingConfig);
                }
            }
            else
                return mapToResponse(existingConfig);
        }

        if (existingConfig.getStatus() == Status.ACTIVE) {

            // Nếu có thay đổi, set inactive config cũ và tạo config mới
            existingConfig.setStatus(Status.INACTIVE);
            existingConfig.setUpdate_at(LocalDateTime.now());
            existingConfig.setUpdated_by("ADMIN");
            configRepository.save(existingConfig);

            // Tạo config mới
            Config newConfig = Config.builder()
                    .group(request.getGroup())
                    .type(request.getType())
                    .key(request.getConfigKey())
                    .value(request.getConfigValue())
                    .status(Status.ACTIVE)
                    .create_at(existingConfig.getCreate_at())
                    .created_by(existingConfig.getCreated_by())
                    .build();
            return mapToResponse(configRepository.save(newConfig));

        } else { // Status là INACTIVE
            // Tìm config active có cùng group, type, key
            Optional<Config> activeConfig = configRepository.findByGroupAndTypeAndKeyAndActiveStatus(
                    request.getGroup(),
                    request.getType(),
                    request.getConfigKey()
            );

            if (activeConfig.isPresent()) {
                Config currentActiveConfig = activeConfig.get();

                // Nếu value giống với value của record active
                if (currentActiveConfig.getValue().equals(request.getConfigValue())) {
                    // Set inactive cho record active hiện tại
                    currentActiveConfig.setValue(existingConfig.getValue());
                    currentActiveConfig.setStatus(Status.INACTIVE);
                    currentActiveConfig.setUpdate_at(LocalDateTime.now());
                    currentActiveConfig.setUpdated_by("ADMIN");
                    configRepository.save(currentActiveConfig);

                    // Set active cho record đang update
                    existingConfig.setGroup(request.getGroup());
                    existingConfig.setType(request.getType());
                    existingConfig.setKey(request.getConfigKey());
                    existingConfig.setValue(request.getConfigValue());
                    existingConfig.setStatus(Status.ACTIVE);
                    existingConfig.setUpdate_at(LocalDateTime.now());
                    existingConfig.setUpdated_by("ADMIN");

                    return mapToResponse(configRepository.save(existingConfig));
                } else {
                    // Nếu value không giống
                    // Set inactive cho cả record active hiện tại và record đang update
                    currentActiveConfig.setStatus(Status.INACTIVE);
                    currentActiveConfig.setUpdate_at(LocalDateTime.now());
                    currentActiveConfig.setUpdated_by("ADMIN");
                    configRepository.save(currentActiveConfig);

                    existingConfig.setStatus(Status.INACTIVE);
                    existingConfig.setUpdate_at(LocalDateTime.now());
                    existingConfig.setUpdated_by("ADMIN");
                    configRepository.save(existingConfig);

                    // Tạo record mới với status ACTIVE
                    Config newConfig = Config.builder()
                            .group(request.getGroup())
                            .type(request.getType())
                            .key(request.getConfigKey())
                            .value(request.getConfigValue())
                            .status(Status.ACTIVE)
                            .create_at(existingConfig.getCreate_at())
                            .created_by(existingConfig.getCreated_by())
//                            .update_at(LocalDateTime.now())
//                            .updated_by("ADMIN")
                            .build();

                    return mapToResponse(configRepository.save(newConfig));
                }
            } else {
                // Nếu không có record active nào thì set active cho record đang update
                existingConfig.setGroup(request.getGroup());
                existingConfig.setType(request.getType());
                existingConfig.setKey(request.getConfigKey());
                existingConfig.setValue(request.getConfigValue());
                existingConfig.setStatus(Status.ACTIVE);
                existingConfig.setUpdate_at(LocalDateTime.now());
                existingConfig.setUpdated_by("ADMIN");

                return mapToResponse(configRepository.save(existingConfig));
            }
        }
    }

    /**
     * Method to display Configure list with no or some filters
     *
     * @param group
     * @param type
     * @param configKey
     * @param status
     * @param pageable
     * @return
     */
    @Override
    public Page<ConfigResponse> getConfigs(String group, String type, String configKey, Status status, Pageable pageable) {
        Page<Config> configs = configRepository.findByGroupAndTypeAndKeyAndStatus(group, type, configKey, status, pageable);
        return configs.map(this::mapToResponse);
    }

    /**
     * Method to soft delete a Configure
     *
     * @param configId
     * @return
     */
    @Override
    public ConfigResponse deleteConfig(UUID configId) {
        Config config = configRepository.findById(configId)
                .orElseThrow(() -> new NotFoundAlertException(MessageCode.MSG2109));

        config.setStatus(Status.INACTIVE);
        configRepository.save(config);
        return mapToResponse(config);
    }

    /**
     * Method is used to a Config object into a Config Response object
     *
     * @param config
     * @return config response
     */
    private ConfigResponse mapToResponse(Config config) {
        return ConfigResponse.builder()
                .configID(config.getConfigId())
                .group(config.getGroup())
                .type(config.getType())
                .configKey(config.getKey())
                .configValue(config.getValue())
                .status(config.getStatus())
                .create_at(config.getCreate_at())
                .create_by(config.getCreated_by())
                .update_at(config.getUpdate_at())
                .update_by(config.getUpdated_by())
                .build();
    }

    /**
     * Method to validate request fields
     *
     * @param request
     */
    private void validateRequest(ConfigRequest request) {
        if (request.getGroup() == null || request.getGroup().isBlank()) {
            throw new BadRequestAlertException(MessageCode.MSG2101);
        }
        if (request.getType() == null || request.getType().isBlank()) {
            throw new BadRequestAlertException(MessageCode.MSG2102);
        }
        if (request.getConfigKey() == null || request.getConfigKey().isBlank()) {
            throw new BadRequestAlertException(MessageCode.MSG2103);
        }
        if (request.getConfigValue() == null || request.getConfigValue().isBlank()) {
            throw new BadRequestAlertException(MessageCode.MSG2104);
        }
    }

    /**
     * Helper method để kiểm tra xem config có thay đổi không
     * Method to check if Configure has changed
     *
     * @param existingConfig
     * @param request
     * @return true ||false
     */
    private boolean isConfigUnchanged(Config existingConfig, ConfigRequest request) {
        return existingConfig.getGroup().equals(request.getGroup()) &&
                existingConfig.getType().equals(request.getType()) &&
                existingConfig.getKey().equals(request.getConfigKey()) &&
                existingConfig.getValue().equals(request.getConfigValue());
    }

}
