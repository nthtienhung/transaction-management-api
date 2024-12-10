package com.transactionservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Author: minh quang
 * Date: 12/7/2024
 * Time: 10:07 PM
 */
public class JacksonConfig {

    /**
     * Creates a new instance of {@link ObjectMapper} with the JavaTimeModule registered.
     *
     * @return a new {@link ObjectMapper} instance.
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
