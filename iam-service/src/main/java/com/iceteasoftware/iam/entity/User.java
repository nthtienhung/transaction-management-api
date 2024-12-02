package com.iceteasoftware.iam.entity;

import com.iceteasoftware.iam.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user", schema = "iam_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditTable {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(name = "is_verified", length = 255)
    private String isVerified;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "role", length = 10)
    private String role;

}
