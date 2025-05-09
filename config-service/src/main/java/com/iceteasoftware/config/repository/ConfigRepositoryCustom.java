package com.iceteasoftware.config.repository;


import com.iceteasoftware.config.entity.Config;
import com.iceteasoftware.config.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConfigRepositoryCustom {
    Page<Config> findByGroupAndTypeAndKeyAndStatus(String group, String type, String configKey, Status status, Pageable pageable);
}
