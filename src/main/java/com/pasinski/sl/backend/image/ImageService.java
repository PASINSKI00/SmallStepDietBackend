package com.pasinski.sl.backend.image;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.MealService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {
    private final UserSecurityService userSecurityService;
    private final MealService mealService;
    private final S3Service s3Service;

    public void addMealImage(String base64Image, Long idMeal) throws IOException {
        String fileName = ApplicationConstants.getMealImageName(idMeal);
        validateImage(base64Image.split(",")[0]);
        MultipartFile image = new Base64EncodedMultipartFile(Base64Utils.decodeFromString(base64Image.split(",")[1]), fileName);

        this.s3Service.uploadInputStream(fileName, FileType.MEAL_IMAGE, image.getInputStream());
        mealService.setImageBooleanValue(idMeal, true);
    }

    public void addMyImage(String base64Image) throws IOException {
        String fileName = "user_id_" + userSecurityService.getLoggedUserId() + "_" + UUID.randomUUID() + ".jpg";
        validateImage(base64Image.split(",")[0]);
        MultipartFile image = new Base64EncodedMultipartFile(Base64Utils.decodeFromString(base64Image.split(",")[1]), fileName);

        this.s3Service.uploadInputStream(fileName, FileType.USER_IMAGE, image.getInputStream());
        userSecurityService.setUsersImage(true, fileName);
    }

    private void validateImage(String header) {
        if (!Objects.equals(header, "data:image/jpeg;base64"))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }
}
