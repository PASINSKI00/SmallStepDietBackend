package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DietRepository extends JpaRepository<Diet, Long> {
    List<Diet> findAllByAppUser(AppUser appUser);
}
