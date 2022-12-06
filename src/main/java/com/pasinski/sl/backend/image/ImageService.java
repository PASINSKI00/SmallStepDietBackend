package com.pasinski.sl.backend.image;

import com.pasinski.sl.backend.meal.MealRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
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

    public void addMealImage(MultipartFile image, Long idMeal) {
        String fileName = UUID.randomUUID().toString() + ".jpg";

        Path storageDirectory = Paths.get(System.getenv("MEALIMAGESPATH"));
        Path destination = Paths.get(storageDirectory.toString() + FileSystems.getDefault().getSeparator() + fileName);

        try {
            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        try {
            mealRepository.findById(idMeal).ifPresent(meal -> {
                meal.setImage(fileName);
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

//    public ResponseEntity uploadToLocalFileSystem(MultipartFile file) {
//        /* we will extract the file name (with extension) from the given file to store it in our local machine for now
//        and later in virtual machine when we'll deploy the project
//         */
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        /* The Path in which we will store our image . we could change it later
//        based on the OS of the virtual machine in which we will deploy the project.
//        In my case i'm using windows 10 .
//         */
//        Path storageDirectory = Paths.get(storageDirectoryPath);
//        /*
//         * we'll do just a simple verification to check if the folder in which we will store our images exists or not
//         * */
//        if(!Files.exists(storageDirectory)){ // if the folder does not exist
//            try {
//                Files.createDirectories(storageDirectory); // we create the directory in the given storage directory path
//            }catch (Exception e){
//                e.printStackTrace();// print the exception
//            }
//        }
//
//        Path destination = Paths.get(storageDirectory.toString() + "\\" + fileName);
//
//        try {
//            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);// we are Copying all bytes from an input stream to a file
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // the response will be the download URL of the image
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("api/images/getImage/")
//                .path(fileName)
//                .toUriString();
//        // return the download image url as a response entity
//        return ResponseEntity.ok(fileDownloadUri);
//    }
}
