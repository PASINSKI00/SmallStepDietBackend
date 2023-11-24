package com.pasinski.sl.backend.monitoring.user;

import com.pasinski.sl.backend.monitoring.Action;
import com.pasinski.sl.backend.user.AppUser;
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
    @Enumerated(EnumType.STRING)
    private Action action;

    private Timestamp timestamp = new Timestamp(new Date().getTime());

    public UserMonitoring(AppUser appUser, Action action) {
        this.appUserId = appUser.getIdUser();
        this.action = action;
    }
}