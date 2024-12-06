package com.iceteasoftware.config.controller;


import com.iceteasoftware.config.dto.request.ConfigRequest;
import com.iceteasoftware.config.dto.response.ConfigResponse;
import com.iceteasoftware.config.dto.response.MessageResponse;
import com.iceteasoftware.config.enums.Status;
import com.iceteasoftware.config.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/config")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private ConfigService configService;

    @Operation(summary = "Add a new configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added a new configuration",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Successful Addition", value = "{\n" +
                                    "  \"message\": \"" + "You have successfully added a new configuration" + "\", \n" +
                                    "  \"status\": 200,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"configID\": 1,\n" +
                                    "    \"group\": \"IAM_GROUP\",\n" +
                                    "    \"type\": \"ROLE\",\n" +
                                    "    \"key\": \"ADMIN\",\n" +
                                    "    \"value\": \"admin\",\n" +
                                    "    \"status\": \"ACTIVE\",\n" +
                                    "    \"create_at\": \"2024-11-07T10:01:02.867Z\",\n" +
                                    "    \"create_by\": \"Admin\",\n" +
                                    "    \"update_at\": \"2024-11-07T10:01:02.867Z\",\n" +
                                    "    \"update_by\": \"Admin\"\n" +
                                    "  }\n" +
                                    "}"),
                            schema = @Schema(implementation = ConfigResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data.",
                    content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "Missing Group Field", value = "{\n" +
                                    "  \"message\": \"" + "Group is required" + "\", \n" +
                                    "  \"status\": 400,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": null\n" +
                                    "}"),
                                    @ExampleObject(name = "Missing Type Field", value = "{\n" +
                                            "  \"message\": \"" + "Type is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}"),
                                    @ExampleObject(name = "Missing Key Field", value = "{\n" +
                                            "  \"message\": \"" + "Key is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}"),
                                    @ExampleObject(name = "Missing Value Field", value = "{\n" +
                                            "  \"message\": \"" + "Value is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}")

                            })),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Failed to add configuration.",
                    content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "Update Old Configuration Failed", value = "{\n" +
                                    "  \"message\": \"" + "Fail to update data in Database" + "\", \n" +
                                    "  \"status\": 500,\n" +
                                    "  \"localDateTime\": \"2024-04-04T23:32:20.1240205\",\n" +
                                    "  \"data\": null\n" +
                                    "}"),
                                    @ExampleObject(name = "Insert New Configuration Failed", value = "{\n" +
                                            "  \"message\": \"" + "Fail to insert data in Database" + "\", \n" +
                                            "  \"status\": 500,\n" +
                                            "  \"localDateTime\": \"2024-04-04T23:32:20.1240205\",\n" +
                                            "  \"data\": null\n" +
                                            "}")
                            }))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigResponse> addConfig(@Valid @RequestBody ConfigRequest request) {
        System.out.println("Accessing addConfig API");
//        ConfigResponse response = configService.addConfig(request);
        return ResponseEntity.ok(configService.addConfig(request));
    }

    @Operation(summary = "Update an existing configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Updating Configuration",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"message\": \"" + "You have successfully updated a configuration" + "\", \n" +
                                    "  \"status\": 200,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"configID\": 1,\n" +
                                    "    \"group\": \"IAM_GROUP\",\n" +
                                    "    \"type\": \"ROLE\",\n" +
                                    "    \"key\": \"ADMIN\",\n" +
                                    "    \"value\": \"administrator\",\n" +
                                    "    \"status\": \"ACTIVE\",\n" +
                                    "    \"create_at\": \"2024-11-07T10:01:02.867Z\",\n" +
                                    "    \"create_by\": \"Admin\",\n" +
                                    "    \"update_at\": \"2024-11-07T10:05:02.867Z\",\n" +
                                    "    \"update_by\": \"Admin\"\n" +
                                    "  }\n" +
                                    "}"),
                            schema = @Schema(implementation = ConfigResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data.",
                    content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "Missing Group Field", value = "{\n" +
                                    "  \"message\": \"" + "Group is required" + "\", \n" +
                                    "  \"status\": 400,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": null\n" +
                                    "}"),
                                    @ExampleObject(name = "Missing Type Field", value = "{\n" +
                                            "  \"message\": \"" + "Type is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}"),
                                    @ExampleObject(name = "Missing Key Field", value = "{\n" +
                                            "  \"message\": \"" + "Key is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}"),
                                    @ExampleObject(name = "Missing Value Field", value = "{\n" +
                                            "  \"message\": \"" + "Value is required" + "\", \n" +
                                            "  \"status\": 400,\n" +
                                            "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                            "  \"data\": null\n" +
                                            "}")

                            })),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Failed to update configuration.",
                    content = @Content(mediaType = "application/json",
                            examples = {@ExampleObject(name = "Update Old Configuration Failed", value = "{\n" +
                                    "  \"message\": \"" + "Fail to update data in Database" + "\", \n" +
                                    "  \"status\": 500,\n" +
                                    "  \"localDateTime\": \"2024-04-04T23:32:20.1240205\",\n" +
                                    "  \"data\": null\n" +
                                    "}"),
                                    @ExampleObject(name = "Insert New Configuration Failed", value = "{\n" +
                                            "  \"message\": \"" + "Fail to insert data in Database" + "\", \n" +
                                            "  \"status\": 500,\n" +
                                            "  \"localDateTime\": \"2024-04-04T23:32:20.1240205\",\n" +
                                            "  \"data\": null\n" +
                                            "}")
                            }))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/update/{configId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigResponse> updateConfig(
            @PathVariable UUID configId,
            @Valid @RequestBody ConfigRequest request) {
//        ConfigResponse response = configService.updateConfig(configId, request);
        return ResponseEntity.ok(configService.updateConfig(configId, request));
    }

    @Operation(summary = "Get Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Querying Action",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Successful Querying", value = "{\n" +
                                    "  \"message\": \"" + "Get configurations succeeded" + "\", \n" +
                                    "  \"status\": 200,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": {" +
                                    "    \"content\": [\n"+
                                    "    {\n" +
                                    "    \"configID\": 1,\n" +
                                    "    \"group\": \"IAM_GROUP\",\n" +
                                    "    \"type\": \"ROLE\",\n" +
                                    "    \"key\": \"ADMIN\",\n" +
                                    "    \"value\": \"admin\",\n" +
                                    "    \"status\": \"ACTIVE\",\n" +
                                    "    \"create_at\": \"2024-11-07T10:01:02.867Z\",\n" +
                                    "    \"create_by\": \"Admin\",\n" +
                                    "    \"update_at\": \"2024-11-07T10:05:02.867Z\",\n" +
                                    "    \"update_by\": \"Admin\"\n" +
                                    "    },\n" +
                                    "    {\n" +
                                    "    \"configID\": 2,\n" +
                                    "    \"group\": \"IAM_GROUP\",\n" +
                                    "    \"type\": \"ROLE\",\n" +
                                    "    \"key\": \"USER\",\n" +
                                    "    \"value\": \"user\",\n" +
                                    "    \"status\": \"ACTIVE\",\n" +
                                    "    \"create_at\": \"2024-11-07T10:01:02.867Z\",\n" +
                                    "    \"create_by\": \"Admin\",\n" +
                                    "    \"update_at\": \"2024-11-07T10:05:02.867Z\",\n" +
                                    "    \"update_by\": \"Admin\"\n" +
                                    "    }\n" +
                                    "   ]," +
                                    "   \"pageable\": {\n" +
                                    "     \"pageNumber\": 0,\n" +
                                    "     \"pageSize\": 10\n" +
                                    "    },\n" +
                                    "   \"totalPages\":1,\n" +
                                    "   \"totalElements\": 2,\n" +
                                    "   \"last\": false,\n" +
                                    "   \"first\": true,\n" +
                                    "   \"empty\": false\n" +
                                    "  }\n" +
                                    "}"),
                            schema = @Schema(implementation = ConfigResponse.class))),
            @ApiResponse(responseCode = "404", description = "Data is not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Data is not found", value = "{\n" +
                                    "  \"message\": \"" + "Configuration not found" + "\", \n" +
                                    "  \"status\": 404,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:00:33.4260787\",\n" +
                                    "  \"data\": null\n" +
                                    "}"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Query Failed", value = "{\n" +
                                    "  \"message\": \"" + "Fail to query configurations in database" + "\", \n" +
                                    "  \"status\": 500,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:00:33.4260787\",\n" +
                                    "  \"data\": null\n" +
                                    "}")))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getconfig")
    public ResponseEntity<MessageResponse<Page<ConfigResponse>>> getActiveConfigs(
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Status status,
            Pageable pageable) {
        Page<ConfigResponse> response = configService.getConfigs(group, type, configKey, status, pageable);
        MessageResponse<Page<ConfigResponse>> messageResponse = MessageResponse.<Page<ConfigResponse>>builder()
                .message("Success")
                .status((short) 200)
                .localDateTime(LocalDateTime.now().toString())
                .data(response)
                .build();
//        return ResponseEntity.ok(response);
        return ResponseEntity.ok(messageResponse);
    }

    @Operation(summary = "Soft delete a configuration", description = "Change configuration status to inactive identified by configId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the configuration.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Successful Deletion", value = "{\n" +
                                    "  \"message\": \"" + "You have successfully deleted a configuration" + "\", \n" +
                                    "  \"status\": 200,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:01:28.0574667\",\n" +
                                    "  \"data\": null\n" +
                                    "}"),
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Data is not found.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Configuration Not Found", value = "{\n" +
                                    "  \"message\": \"" + "Configuration ID not found" + "\", \n" +
                                    "  \"status\": 404,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:00:33.4260787\",\n" +
                                    "  \"data\": null\n" +
                                    "}"))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Failed to delete configuration.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Deletion Failed", value = "{\n" +
                                    "  \"message\": \"" + "Fail to delete configurations in database" + "\", \n" +
                                    "  \"status\": 500,\n" +
                                    "  \"localDateTime\": \"2024-04-04T18:00:33.4260787\",\n" +
                                    "  \"data\": null\n" +
                                    "}")))
    })
    @DeleteMapping("/delete/{configId}")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigResponse> deleteConfig(@PathVariable UUID configId) {
//        ConfigResponse response = configService.deleteConfig(configId);
        return ResponseEntity.ok(configService.deleteConfig(configId));
    }
}



