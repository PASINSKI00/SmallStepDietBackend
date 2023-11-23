package com.pasinski.sl.backend.interceptors;

import com.pasinski.sl.backend.config.ApplicationContextProvider;
import com.pasinski.sl.backend.monitoring.user.UserMonitoring;
import com.pasinski.sl.backend.monitoring.user.UserMonitoringRepository;
import com.pasinski.sl.backend.user.AppUser;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;

public class AppUserInterceptor extends EmptyInterceptor {

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if(entity instanceof AppUser appUser) {
            ApplicationContextProvider.getContext().getBean(UserMonitoringRepository.class)
                    .save(new UserMonitoring(appUser.getIdUser()));
        }

        return super.onSave(entity, id, state, propertyNames, types);
    }
}
