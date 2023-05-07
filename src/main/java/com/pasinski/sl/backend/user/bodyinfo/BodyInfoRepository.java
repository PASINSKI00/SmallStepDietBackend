package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodyInfoRepository extends JpaRepository<BodyInfo, Long> {
    Optional<BodyInfo> findByAppUser(AppUser appUser);
}

