package com.example.configservice.entity;

import com.example.configservice.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_config", schema = "config_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "configid")
    private UUID configId;

    @Column(name = "\"group\"")
    private String group; // 'group' is a reserved keyword in Java

    @Column(name = "\"type\"")
    private String type;

    @Column(name = "\"key\"")
    private String key;

    @Column(name = "\"value\"")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status; // e.g., 'active', 'inactive'

    @Column(name = "create_at")
    private LocalDateTime create_at;

    @Column(name = "created_by")
    private String created_by;

    @Column(name = "update_at")
    private LocalDateTime update_at;

    @Column(name = "updated_by")
    private String updated_by;
}
