package com.iceteasoftware.config.repository.impl;


import com.iceteasoftware.config.entity.Config;
import com.iceteasoftware.config.enums.Status;
import com.iceteasoftware.config.repository.ConfigRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ConfigRepositoryCustomImpl implements ConfigRepositoryCustom {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Page<Config> findByGroupAndTypeAndKeyAndStatus(String group, String type, String configKey, Status status, Pageable pageable) {
        String baseQuery = "SELECT * FROM config_service.tbl_config WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (group != null && !group.isEmpty()) {
            baseQuery += " AND \"group\" = ?";
            params.add(group);
        }
        if (type != null && !type.isEmpty()) {
            baseQuery += " AND \"type\" = ?";
            params.add(type);
        }
        if (configKey != null && !configKey.isEmpty()) {
            baseQuery += " AND \"key\" = ?";
            params.add(configKey);
        }
        if (status != null) {
            baseQuery += " AND status = ?";
            params.add(status.name());
        }

        baseQuery += " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();

        List<Config> configs = jdbcTemplate.query(baseQuery, params.toArray(), new BeanPropertyRowMapper<>(Config.class));
        long total = jdbcTemplate.queryForObject("SELECT count(*) FROM config_service.tbl_config WHERE 1=1", Long.class);

        return new PageImpl<>(configs, pageable, total);
    }
}
