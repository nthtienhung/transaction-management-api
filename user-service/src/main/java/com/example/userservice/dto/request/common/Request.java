package com.example.userservice.dto.request.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, value = {"clientMessageId", "requestLog", "url", "method"})
public abstract class Request implements Serializable {

    private static final long serialVersionUID = -8440513573690364524L;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    protected transient String clientMessageId;


    @JsonIgnore
    @ApiModelProperty(hidden = true)
    protected transient String apiGeeTransactionId;

    @JsonIgnore
    @Transient
    @ApiModelProperty(hidden = true)
    protected transient Object requestLog;

    @JsonIgnore
    @Transient
    @ApiModelProperty(hidden = true)
    protected transient String url;

    @JsonIgnore
    @Transient
    @ApiModelProperty(hidden = true)
    protected transient HttpMethod method = HttpMethod.GET;


}
