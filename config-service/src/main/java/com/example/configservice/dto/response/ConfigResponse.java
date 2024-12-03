package com.example.configservice.dto.response;

import com.example.configservice.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigResponse {
    private UUID configID;
    private String group;
    private String type;
    private String configKey;
    private String configValue;
    @Schema(description = "status of the config")
    private Status status;
    private LocalDateTime create_at;
    private String create_by;
    private LocalDateTime update_at;
    private String update_by;
}
