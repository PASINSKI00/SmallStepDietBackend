package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
public class Diet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diet", nullable = false)
    private Long idDiet;

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<FinalDay> finalDays;

    @ManyToOne
    private AppUser appUser;
}
