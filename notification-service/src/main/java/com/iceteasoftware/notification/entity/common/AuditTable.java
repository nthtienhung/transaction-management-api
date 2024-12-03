package com.iceteasoftware.notification.entity.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Author: Tran Duc Thinh
 * Date: 11/29/2024
 * Time: 9:16 AM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditTable {

    @CreatedBy
    @Column(name = "user_create", updatable = false)
    private String userCreate;

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    @JsonIgnore
    private Instant createdDate = Instant.now();


}