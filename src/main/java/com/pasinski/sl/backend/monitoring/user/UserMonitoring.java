package com.pasinski.sl.backend.monitoring.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserMonitoring {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_monitoring_entity", nullable = false)
    private Long id_user_monitoring_entity;

    private Long appUserId;

    private Timestamp createdOn = new Timestamp(new Date().getTime());
}