package com.pasinski.sl.backend.meal.category;

import com.pasinski.sl.backend.meal.forms.CategoryForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories;

        try {
            categories = categoryService.getAllCategories();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryForm categoryForm) {
        try {
            categoryService.addCategory(categoryForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
