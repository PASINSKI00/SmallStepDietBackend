package com.pasinski.sl.backend.config;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.interceptors.AppUserInterceptor;
import com.pasinski.sl.backend.user.AppUserRepository;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pasinski.sl.backend.util.components.CustomErrorAttributes;

@Configuration
public class AppConfig {
    AppUserRepository appUserRepository;

    @Bean
    public UserSecurityService userSecurity() {
        return new UserSecurityService(appUserRepository);
    }

    @Bean
    public ErrorAttributes errorAttributes() {
        return new CustomErrorAttributes();
    }

    @Bean
    public AppUserInterceptor appUserInterceptor() {
        return new AppUserInterceptor();
    }
}
