package com.pasinski.sl.backend.image;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.post.PostRepository;
import com.pasinski.sl.backend.security.UserSecurityService;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {
    private MealRepository mealRepository;
    private PostRepository postRepository;
    private AppUserRepository appUserRepository;
    private UserSecurityService userSecurityService;

    public InputStreamResource getMealImage(Long idMeal) throws FileNotFoundException {
        String name = "meal_id"+ idMeal + ".jpg";
        File file = new File(ApplicationConstants.PATH_TO_MEAL_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + name);

        if (!file.exists())
            name = ApplicationConstants.DEFAULT_MEAL_IMAGE_NAME;

        file = new File(ApplicationConstants.PATH_TO_MEAL_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + name);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public InputStreamResource getPostImage(Long idPost) throws FileNotFoundException {
        String name = postRepository.findById(idPost).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND)).getImage();
        File file = new File(ApplicationConstants.PATH_TO_POST_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + name);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public void addMealImage(String base64Image, Long idMeal) {
        String fileName = "meal_id"+ idMeal + ".jpg";
        String base64header = base64Image.split(",")[0];
        String base64ImageWithoutHeader = base64Image.split(",")[1];

        byte[] fileContent = Base64Utils.decodeFromString(base64ImageWithoutHeader);
        MultipartFile image = new Base64EncodedMultipartFile(fileContent, fileName);

        validateImage(base64header);

        Path storageDirectory = Paths.get(ApplicationConstants.PATH_TO_MEAL_IMAGES_DIRECTORY);
        Path destination = Paths.get(storageDirectory + FileSystems.getDefault().getSeparator() + fileName);

        try {
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        try {
            mealRepository.findById(idMeal).ifPresent(meal -> {
                mealRepository.save(meal);
            });
        } catch (Exception e) {
            try {
                Files.delete(destination);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    public InputStreamResource getUserImage(Long idUser) throws FileNotFoundException {
        AppUser appUser = this.appUserRepository.findById(idUser).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        String fileName = appUser.getImage();
        File file = new File(ApplicationConstants.PATH_TO_USER_IMAGES_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public void addMyImage(String base64Image) {
        String fileName = "user_" + UUID.randomUUID() + ".jpg";
        String base64header = base64Image.split(",")[0];
        String base64ImageWithoutHeader = base64Image.split(",")[1];

        byte[] fileContent = Base64Utils.decodeFromString(base64ImageWithoutHeader);
        MultipartFile image = new Base64EncodedMultipartFile(fileContent, fileName);

        validateImage(base64header);

        Path storageDirectory = Paths.get(ApplicationConstants.PATH_TO_USER_IMAGES_DIRECTORY);
        Path destination = Paths.get(storageDirectory + FileSystems.getDefault().getSeparator() + fileName);

        try {
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        try {
            appUserRepository.findById(this.userSecurityService.getLoggedUser().getIdUser()).ifPresent(appUser -> {
                appUser.setImage(fileName);
                appUserRepository.save(appUser);
            });
        } catch (Exception e) {
            try {
                Files.delete(destination);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private void validateImage(String header) {
        if (!Objects.equals(header, "data:image/jpeg;base64"))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }

    public void addPostImage(String base64Image, Long idPost) {
        String fileName = "post_" + UUID.randomUUID() + ".jpg";
        String base64header = base64Image.split(",")[0];
        String base64ImageWithoutHeader = base64Image.split(",")[1];

        byte[] fileContent = Base64Utils.decodeFromString(base64ImageWithoutHeader);
        MultipartFile image = new Base64EncodedMultipartFile(fileContent, fileName);

        validateImage(base64header);

        Path storageDirectory = Paths.get(ApplicationConstants.PATH_TO_POST_IMAGES_DIRECTORY);
        Path destination = Paths.get(storageDirectory + FileSystems.getDefault().getSeparator() + fileName);

        try {
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        try {
            postRepository.findById(idPost).ifPresent(post -> {
                post.setImage(fileName);
                postRepository.save(post);
            });
        } catch (Exception e) {
            try {
                Files.delete(destination);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }
}
