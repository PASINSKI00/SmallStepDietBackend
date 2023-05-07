package com.pasinski.sl.backend.meal.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Ingredient findByName(String name);

    List<Ingredient> findAllByNameIn(List<String> names);

    @Query("select new Ingredient(i.idIngredient, i.name) from Ingredient i")
    List<Ingredient> getAllIdsAndNames();
}
