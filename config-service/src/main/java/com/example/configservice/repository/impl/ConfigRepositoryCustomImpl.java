package com.example.configservice.repository.impl;


import com.example.configservice.entity.Config;
import com.example.configservice.enums.Status;
import com.example.configservice.repository.ConfigRepositoryCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ConfigRepositoryCustomImpl implements ConfigRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Page<Config> findByGroupAndTypeAndKeyAndStatus(String group, String type, String configKey, Status status, Pageable pageable) {
        String baseSelect = "SELECT * FROM configservice.tbl_config";
        String countSelect = "SELECT COUNT(*) FROM configservice.tbl_config";
        String whereClause = " WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (group != null && !group.isEmpty()) {
            whereClause += " AND \"group\" = ?";
            params.add(group);
        }
        if (type != null && !type.isEmpty()) {
            whereClause += " AND \"type\" = ?";
            params.add(type);
        }
        if (configKey != null && !configKey.isEmpty()) {
            whereClause += " AND \"key\" = ?";
            params.add(configKey);
        }
        if (status != null) {
            whereClause += " AND status = ?";
            params.add(status.name());
        }

        String baseQuery = baseSelect + whereClause + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        String countQuery = countSelect + whereClause;

        List<Config> configs = jdbcTemplate.query(baseQuery, params.toArray(), new BeanPropertyRowMapper<>(Config.class));
        long total = jdbcTemplate.queryForObject(countQuery, params.toArray(), Long.class);

        log.info("Total configs and pagable: {}, {}", total, pageable);

        return new PageImpl<>(configs, pageable, total);
    }
}