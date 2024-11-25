package com.example.iamservice.entity;

import com.example.iamservice.entity.common.AuditTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tbl_user_login_failed")
@EqualsAndHashCode(callSuper = true)
public class UserLoginFailed extends AuditTable {

    @Id
    @Column(name = "user_id",length = 255)
    private String userId;

    @Column(name = "login_failed_attempts")
    private int loginFailedAttempts;

    @Column(name = "unlock_time")
    private LocalDateTime unlockTime;

    private static final long serialVersionUID = 12312312412312321L;

    public UserLoginFailed(String userId) {
        super();
        this.userId = userId;
    }
}
