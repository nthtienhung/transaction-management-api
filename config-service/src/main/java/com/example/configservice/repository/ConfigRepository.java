package com.example.configservice.repository;

import com.example.configservice.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigRepository extends JpaRepository<Config, UUID>, ConfigRepositoryCustom{
    Optional<Config> findByGroupAndTypeAndKeyAndValue(String group, String type, String configKey, String value);

    Optional<Config> findByGroupAndTypeAndKey(String group, String type, String configKey);

    @Query(value = "select * from configservice.tbl_config c WHERE c.\"group\" = :group AND c.\"type\" = :type AND c.\"key\" = :configKey AND c.status = 'ACTIVE'", nativeQuery = true)
    Optional<Config> findByGroupAndTypeAndKeyAndActiveStatus(@Param("group") String group, @Param("type") String type, @Param("configKey") String configKey);

}
