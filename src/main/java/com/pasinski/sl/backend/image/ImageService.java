package com.pasinski.sl.backend.image;

import com.pasinski.sl.backend.meal.MealRepository;
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
    public InputStreamResource getMealImage(String name) throws FileNotFoundException {
        File file = new File(System.getenv("MEALIMAGESPATH") + name);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public void addMealImage(String base64Image, Long idMeal) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        String base64header = base64Image.split(",")[0];
        String base64ImageWithoutHeader = base64Image.split(",")[1];

        byte[] fileContent = Base64Utils.decodeFromString(base64ImageWithoutHeader);
        MultipartFile image = new Base64EncodedMultipartFile(fileContent, fileName);

        validateImage(base64header);

        Path storageDirectory = Paths.get(System.getenv("MEALIMAGESPATH"));
        Path destination = Paths.get(storageDirectory.toString() + FileSystems.getDefault().getSeparator() + fileName);

        try {
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        try {
            mealRepository.findById(idMeal).ifPresent(meal -> {
                meal.setImageName(fileName);
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

    private void validateImage(String header) {
        if (!Objects.equals(header, "data:image/jpeg;base64"))
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
    }
}
