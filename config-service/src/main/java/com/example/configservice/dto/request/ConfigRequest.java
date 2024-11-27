package com.example.configservice.dto.request;

import com.example.configservice.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigRequest {

    @Schema(description = "Group of the configuration")
    @NotBlank(message = "Group is required")
    private String group;

    @Schema(description = "Type of the configuration")
    @NotBlank(message = "Type is required")
    private String type;

    @Schema(description = "Key for the configuration")
    @NotBlank(message = "Key is required")
    private String configKey;

    @Schema(description = "Value for the configuration")
    @NotBlank(message = "Value is required")
    private String configValue;

    @Schema(description = "Status of the configuration (ACTIVE/INACTIVE)")
    private Status status;

//    @Schema(description = "User who created this configuration")
//    private String create_by;
}
