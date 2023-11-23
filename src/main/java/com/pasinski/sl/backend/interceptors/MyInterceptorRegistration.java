package com.pasinski.sl.backend.interceptors;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class MyInterceptorRegistration implements HibernatePropertiesCustomizer {

    private AppUserInterceptor appUserInterceptor;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", appUserInterceptor);
    }
}
