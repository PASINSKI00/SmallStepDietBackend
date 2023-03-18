package com.pasinski.sl.backend.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealService;
import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImageService {
    private final AppUserRepository appUserRepository;
    private final UserSecurityService userSecurityService;
    private final MealService mealService;
    private final AmazonS3Client amazonS3Client;

    public InputStreamResource getMealImage(Long idMeal) throws FileNotFoundException {
        String fileName = "default_meal.jpg";
        Meal meal = mealService.getMealById(idMeal);

        if (meal.isImageSet())
            fileName = "meal_id_" + idMeal + ".jpg";

        GetObjectRequest request = new GetObjectRequest("dev-ssd", "images/meals/" + fileName);
        S3Object s3Object = amazonS3Client.getObject(request);

        return new InputStreamResource(s3Object.getObjectContent());
    }

    public InputStreamResource getUserImage(Long idUser) throws FileNotFoundException {
        String fileName = "default_user.jpg";
        AppUser appUser = this.appUserRepository.findById(idUser).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (appUser.isImageSet())
            fileName = "user_id_" + idUser + ".jpg";

        GetObjectRequest request = new GetObjectRequest("dev-ssd", "images/users/" + fileName);
        S3Object s3Object = amazonS3Client.getObject(request);

        return new InputStreamResource(s3Object.getObjectContent());
    }

    public void addMealImage(String base64Image, Long idMeal) throws IOException {
        File file;
        String bucketName = "dev-ssd";
        String fileName = "meal_id_" + idMeal + ".jpg";
        validateImage(base64Image.split(",")[0]);
        MultipartFile image = new Base64EncodedMultipartFile(Base64Utils.decodeFromString(base64Image.split(",")[1]), fileName);

        Files.copy(image.getInputStream(),
                Paths.get(Paths.get(ApplicationConstants.PATH_TO_MEAL_IMAGES_DIRECTORY) + FileSystems.getDefault().getSeparator() + fileName),
                StandardCopyOption.REPLACE_EXISTING);
        file = new File(ApplicationConstants.PATH_TO_MEAL_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        PutObjectRequest request = new PutObjectRequest(bucketName, "images/meals/" + fileName, file);
        amazonS3Client.putObject(request);

        mealService.setImageBooleanValue(idMeal, true);

        file.delete();
    }

    public void addMyImage(String base64Image) throws IOException {
        File file;
        String bucketName = "dev-ssd";
        String fileName = "user_id_" + userSecurityService.getLoggedUserId() + ".jpg";
        validateImage(base64Image.split(",")[0]);
        MultipartFile image = new Base64EncodedMultipartFile(Base64Utils.decodeFromString(base64Image.split(",")[1]), fileName);

        Files.copy(image.getInputStream(),
                Paths.get(Paths.get(ApplicationConstants.PATH_TO_USER_IMAGES_DIRECTORY) + FileSystems.getDefault().getSeparator() + fileName),
                StandardCopyOption.REPLACE_EXISTING);
        file = new File(ApplicationConstants.PATH_TO_USER_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        PutObjectRequest request = new PutObjectRequest(bucketName, "images/users/" + fileName, file);
        amazonS3Client.putObject(request);

        userSecurityService.setImageSetBooleanValue(true);

        file.delete();
    }

    private void validateImage(String header) {
        if (!Objects.equals(header, "data:image/jpeg;base64"))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }
}
