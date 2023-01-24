package com.pasinski.sl.backend.post;

import com.pasinski.sl.backend.post.forms.PostForm;
import com.pasinski.sl.backend.post.forms.PostResponseForm;
import com.pasinski.sl.backend.security.UserSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserSecurityService userSecurityService;

    public List<PostResponseForm> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(PostResponseForm::new).collect(Collectors.toList());
    }

    public Long createPost(PostForm postForm) {
        Post post = new Post(postForm, userSecurityService.getLoggedUser());
        return this.postRepository.save(post).getIdPost();
    }

    public void deletePost(Long idPost) {
        Post post = postRepository.findById(idPost).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NO_CONTENT));

        if (!post.getAuthor().equals(userSecurityService.getLoggedUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        postRepository.delete(post);
    }
}
