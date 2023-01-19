package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.bodyinfo.forms.Goals;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class BodyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_body_info", nullable = false)
    private Long idBodyInfo;

    @NotNull
    @Column(nullable = false)
    private Goals goal;

    @NotNull
    @Column(nullable = false)
    private Integer height;

    @NotNull
    @Column(nullable = false)
    private Integer weight;

    @NotNull
    @Column(nullable = false)
    private Integer age;

    @NotNull
    @Column(nullable = false)
    private Float pal;

    @NotNull
    @OneToOne(mappedBy = "bodyInfo", optional = false)
    private AppUser appUser;
}
