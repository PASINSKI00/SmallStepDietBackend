package com.pasinski.sl.backend.config;

import com.pasinski.sl.backend.security.UserSecurity;
import com.pasinski.sl.backend.user.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    AppUserRepository appUserRepository;

    @Bean
    public UserSecurity userSecurity() {
        return new UserSecurity(appUserRepository);
    }
}
