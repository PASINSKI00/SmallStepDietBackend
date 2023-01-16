package com.pasinski.sl.backend.basic;

public class ApplicationConstants {
    public static final String DEFAULT_APP_URL = "http://localhost:8080";
    public static final String DEFAULT_MEAL_IMAGE_URL_WITH_PARAMETER = DEFAULT_APP_URL + "/api/image/meal?idMeal=";

    public static final String DEFAULT_DIET_PDF_URL_WITH_PARAMETER = DEFAULT_APP_URL + "/api/diet/pdf?idDiet=";

    public static final String DEFAULT_GROCERIES_PDF_URL_WITH_PARAMETER = DEFAULT_APP_URL + "/api/diet/groceries/pdf?idDiet=";
    public static final String DEFAULT_MEAL_IMAGE_NAME = "default_meal.jpg";
    public static final String APP_DIRECTORY = "C:\\Users\\Krystian\\apps\\Dieter";
    public static final String PATH_TO_MEAL_IMAGES_DIRECTORY = APP_DIRECTORY + "\\images\\meals";
    public static final String PATH_TO_PDF_DIRECTORY = APP_DIRECTORY + "\\pdfs";
}
