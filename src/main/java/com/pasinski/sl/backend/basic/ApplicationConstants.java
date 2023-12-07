package com.pasinski.sl.backend.basic;

import com.pasinski.sl.backend.user.AppUser;

public class ApplicationConstants {
    public static final String DEFAULT_APP_URL = "http://localhost:8080";
    public static final String EMAIL_CONFIRMATION_URL = DEFAULT_APP_URL + "/api/user/verify?token=";

    public static final String DEFAULT_MEAL_IMAGE_NAME = "default_meal.jpg";
    public static final String DEFAULT_USER_IMAGE_NAME = "default_user.jpg";

    public static String getMealImageName(Long idMeal) {
        return "meal_id_" + idMeal + ".jpg";
    }

    public static String getUserImageName(AppUser appUser) {
        return appUser.isImageSet() ? appUser.getImageName() : DEFAULT_USER_IMAGE_NAME;
    }
}
