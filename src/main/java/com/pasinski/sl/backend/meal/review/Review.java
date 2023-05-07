package com.pasinski.sl.backend.meal.review;

import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review", nullable = false)
    private Long idReview;
    private String comment;
    private int rating;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser author;
}
