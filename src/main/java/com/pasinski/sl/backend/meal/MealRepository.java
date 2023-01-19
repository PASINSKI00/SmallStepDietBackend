package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
    Iterable<Meal> findAllByAuthor(AppUser appUser);
}
