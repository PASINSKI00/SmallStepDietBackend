package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@AllArgsConstructor
public class BodyInfoService {
    private BodyInfoRepository bodyInfoRepository;
    private UserSecurityService userSecurityService;
    private AppUserRepository appUserRepository;

    public BodyInfo getBodyInfo() {
        return bodyInfoRepository.findByAppUser(userSecurityService.getLoggedUser()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    public void addBodyInfo(BodyInfoForm bodyInfoForm) {
        BodyInfo bodyInfo = new BodyInfo(bodyInfoForm, userSecurityService.getLoggedUser());
        bodyInfoRepository.save(bodyInfo);

        AppUser appUser = userSecurityService.getLoggedUser();
        appUser.setBodyInfo(bodyInfo);
        appUserRepository.save(appUser);
    }
}
