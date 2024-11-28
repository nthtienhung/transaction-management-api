package com.iceteasoftware.user.entity;

import com.iceteasoftware.user.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_profile")
public class Profile extends AuditTable {

    @Id
    @Column(name = "profile_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String profileId;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "phone", length = 10, nullable = false)
    private String phone;

    @Column(name = "dob")
    private LocalDate dob;

}

