package com.pasinski.sl.backend.user.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseForm {
    String name;
    String imageUrl;

    public UserResponseForm(AppUser appUser) {
        this.name = appUser.getName();
        this.imageUrl = ApplicationConstants.DEFAULT_USER_IMAGE_URL_WITH_PARAMETER + appUser.getIdUser();
    }
}
