package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    Iterable<Meal> findAllByAuthor(AppUser appUser);
    @Query("SELECT DISTINCT m FROM Meal m " +
            "LEFT JOIN m.categories c " +
            "WHERE m.name LIKE CONCAT('%', :name, '%') " +
            "AND (c IN :categories OR :categories IS NULL) " +
            "GROUP BY m " +
            "HAVING COUNT(DISTINCT c) >= :categoriesCount")
    Page<Meal> findMealsByNameAndCategories(@Param("name") String name, @Param("categories") List<Category> categories,
                                            @Param("categoriesCount") Long categoriesCount, Pageable pageable);
}
