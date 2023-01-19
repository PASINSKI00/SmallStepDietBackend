package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyInfoRepository extends JpaRepository<BodyInfo, Long> {
    BodyInfo findByAppUser(AppUser appUser);
}

