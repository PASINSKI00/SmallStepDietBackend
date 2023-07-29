package com.pasinski.sl.backend.meal.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String fast);
    List<Category> findAllByNameIn(List<String> categories);
}
