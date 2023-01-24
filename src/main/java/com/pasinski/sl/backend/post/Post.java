package com.pasinski.sl.backend.post;

import com.pasinski.sl.backend.post.forms.PostForm;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_post", nullable = false)
    private Long idPost;

    private String content;
    private String image;
    private Date postDate;

    @ManyToOne
    private AppUser author;

    public Post(PostForm postForm, AppUser author) {
        this.content = postForm.getContent();
        this.author = author;
        this.postDate = new Date();
    }
}
