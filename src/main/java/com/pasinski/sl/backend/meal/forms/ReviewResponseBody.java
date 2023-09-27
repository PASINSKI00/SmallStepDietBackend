package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponseBody {
    private String imageUrl;
    private String name;
    private int rating;
    private String content;

    public ReviewResponseBody(Review review, S3Service s3Service) {
        this.imageUrl = s3Service.getFileUrl(ApplicationConstants.getUserImageName(review.getAuthor()), FileType.USER_IMAGE);
        this.name = review.getAuthor().getName();
        this.rating = review.getRating();
        this.content = review.getComment();
    }
}
