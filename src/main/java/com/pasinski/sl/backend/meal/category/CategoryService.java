package com.pasinski.sl.backend.meal.category;

import com.pasinski.sl.backend.meal.forms.CategoryForm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public void addCategory(CategoryForm categoryForm) {
        Category category = new Category();

        category.setName(categoryForm.getName());

        categoryRepository.save(category);
    }
}
