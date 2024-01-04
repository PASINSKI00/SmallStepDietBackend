package com.pasinski.sl.backend.integration;

import com.pasinski.sl.backend.meal.forms.CategoryForm;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryEndpointIT extends BaseForIT {
    String categoryEndpoint = "/api/category";
    String allCategoriesEndpoint = "/api/category/all";

    @Test
    @Order(1)
    public void adminShouldBeAbleToPostCategory() throws IOException, InterruptedException {
        //given
        CategoryForm categoryForm = new CategoryForm("Category1");
        String url = host + categoryEndpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", adminAuthorizationHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(categoryForm)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(201, httpResponse.statusCode());
    }

    @Test
    @Order(2)
    public void getCategoriesEndpoint() throws IOException, InterruptedException {
        //given
        String url = host + allCategoriesEndpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        List<String> expectedCategories = new ArrayList<>(Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack",
                "Dessert", "Shake", "Fast food", "Soup", "Salad", "Bread", "Pasta", "Pizza", "Sandwich", "Sauce",
                "Drink"));

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
        expectedCategories.forEach(category -> assertTrue(httpResponse.body().contains(category)));
    }

    @Test
    public void userShouldNotBeAbleToPostCategory() throws IOException, InterruptedException {
        //given
        CategoryForm categoryForm = new CategoryForm("UnableToAdd");
        String url = host + categoryEndpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", userAuthorizationHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(categoryForm)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(403, httpResponse.statusCode());
    }
}
