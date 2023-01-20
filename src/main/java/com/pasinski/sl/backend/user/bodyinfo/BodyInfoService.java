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
        bodyInfoResponseForm.setAdditionalCalories(bodyInfo.getAdditionalCalories());
        bodyInfoResponseForm.setTDEE(bodyInfo.getTDEE());
        bodyInfoResponseForm.setBEE(bodyInfo.getBEE());
        bodyInfoResponseForm.setCaloriesGoal(bodyInfo.getCaloriesGoal());

        return bodyInfoResponseForm;
    }

    public void addBodyInfo(BodyInfoForm bodyInfoForm) {
        BodyInfo bodyInfo = new BodyInfo();
        bodyInfo.setGoal(bodyInfoForm.getGoal());
        bodyInfo.setHeight(bodyInfoForm.getHeight());
        bodyInfo.setWeight(bodyInfoForm.getWeight());
        bodyInfo.setAge(bodyInfoForm.getAge());
        bodyInfo.setPal(bodyInfoForm.getPal());
        bodyInfo.setAdditionalCalories(bodyInfoForm.getAdditionalCalories());
        bodyInfo.setAppUser(userSecurityService.getLoggedUser());
        performCalculations(bodyInfo);

        bodyInfoRepository.save(bodyInfo);

        AppUser appUser = userSecurityService.getLoggedUser();
        appUser.setBodyInfo(bodyInfo);
        appUserRepository.save(appUser);

        System.out.println(userSecurityService.getLoggedUser().getBodyInfo());
    }

    private void performCalculations(BodyInfo bodyInfo) {
        bodyInfo.setBEE(calculateBEE(bodyInfo));
        bodyInfo.setTDEE(calculateTDEE(bodyInfo));
        bodyInfo.setCaloriesGoal(calculateCaloriesGoal(bodyInfo));
    }

    private Integer calculateTDEE(BodyInfo bodyInfo) {
        return (int) (bodyInfo.getBEE() * bodyInfo.getPal());
    }

    private Integer calculateBEE(BodyInfo bodyInfo) {
        return (int) (10 * bodyInfo.getWeight() + 6.25 * bodyInfo.getHeight() - 5 * bodyInfo.getAge() + 5);
    }

    private Integer calculateCaloriesGoal(BodyInfo bodyInfo) {
        Double multiplierBasedOnGoal = 0D;

        switch (bodyInfo.getGoal()) {
            case LOSE_WEIGHT:
                multiplierBasedOnGoal = 0.9;
                break;
            case MAINTAIN_WEIGHT:
                multiplierBasedOnGoal = 1.0;
                break;
            case GAIN_WEIGHT:
                multiplierBasedOnGoal = 1.1;
                break;
        }
        return (int) (bodyInfo.getTDEE() * multiplierBasedOnGoal + bodyInfo.getAdditionalCalories());
    }
}
