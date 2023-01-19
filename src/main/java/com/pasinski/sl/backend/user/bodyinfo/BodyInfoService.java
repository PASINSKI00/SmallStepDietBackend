package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoResponseForm;
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

    public BodyInfoResponseForm getBodyInfo() {
        AppUser appUser = userSecurityService.getLoggedUser();
        BodyInfo bodyInfo = bodyInfoRepository.findByAppUser(appUser);

        if (bodyInfo == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        BodyInfoResponseForm bodyInfoResponseForm = new BodyInfoResponseForm();
        bodyInfoResponseForm.setGoal(bodyInfo.getGoal());
        bodyInfoResponseForm.setHeight(bodyInfo.getHeight());
        bodyInfoResponseForm.setWeight(bodyInfo.getWeight());
        bodyInfoResponseForm.setAge(bodyInfo.getAge());
        bodyInfoResponseForm.setPal(bodyInfo.getPal());

        return bodyInfoResponseForm;
    }

    public void addBodyInfo(BodyInfoForm bodyInfoForm) {
        BodyInfo bodyInfo = new BodyInfo();
        bodyInfo.setGoal(bodyInfoForm.getGoal());
        bodyInfo.setHeight(bodyInfoForm.getHeight());
        bodyInfo.setWeight(bodyInfoForm.getWeight());
        bodyInfo.setAge(bodyInfoForm.getAge());
        bodyInfo.setPal(bodyInfoForm.getPal());
        bodyInfo.setAppUser(userSecurityService.getLoggedUser());

        bodyInfoRepository.save(bodyInfo);

        AppUser appUser = userSecurityService.getLoggedUser();
        appUser.setBodyInfo(bodyInfo);
        appUserRepository.save(appUser);

        System.out.println(userSecurityService.getLoggedUser().getBodyInfo());
    }
}
