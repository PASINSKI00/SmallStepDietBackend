package com.pasinski.sl.backend.post.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostResponseForm {
    private Long idPost;
    private String userImageUrl;
    private String username;
    private Date postDate;
    private String content;
    private String imageUrl;

    public PostResponseForm(Post post) {
        this.idPost = post.getIdPost();
        this.userImageUrl = ApplicationConstants.DEFAULT_USER_IMAGE_URL_WITH_PARAMETER + post.getAuthor().getIdUser();
        this.username = post.getAuthor().getName();
        this.postDate = post.getPostDate();
        this.content = post.getContent();
        if(post.getImage() != null)
            this.imageUrl = ApplicationConstants.DEFAULT_POST_IMAGE_URL_WITH_PARAMETER + post.getIdPost();
    }
}
