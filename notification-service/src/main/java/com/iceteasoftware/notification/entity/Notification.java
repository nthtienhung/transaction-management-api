package com.iceteasoftware.notification.entity;

import com.iceteasoftware.notification.entity.common.AuditTable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_notification", schema = "notification_service")
@Entity
public class Notification extends AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id")
    private String id;

    @Column(name = "status")
    private String status;

    @Column(name ="work")
    private String work;

}
