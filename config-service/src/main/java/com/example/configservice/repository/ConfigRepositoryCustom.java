package com.example.configservice.repository;


import com.example.configservice.entity.Config;
import com.example.configservice.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConfigRepositoryCustom {
    Page<Config> findByGroupAndTypeAndKeyAndStatus(String group, String type, String configKey, Status status, Pageable pageable);
}
