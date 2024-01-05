package com.pasinski.sl.backend.integration;

import com.pasinski.sl.backend.user.bodyinfo.Gender;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.Goals;
import com.pasinski.sl.backend.user.forms.UserForm;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserEndpointIT extends BaseForIT{
    String userEndpoint = "/api/user";
    String userBodyInfoEndpoint = "/api/user/bodyinfo";

    @Test
    @Order(1)
    public void shouldBePossibleToCreateANewAccount() throws IOException, InterruptedException {
        UserForm userForm = new UserForm("Charlie", "unused@email.com", "Password1");
        String url = host + userEndpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(userForm)))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, httpResponse.statusCode());
    }

    @Test
    @Order(2)
    public void authenticatedUserShouldBeAbleToPostBodyInfo() throws IOException, InterruptedException {
        //given
        BodyInfoForm bodyInfoForm = new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 182, 88, 23,
                Gender.MALE, 1.5F, 0);
        String url = host + userBodyInfoEndpoint;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", userAuthorizationHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(bodyInfoForm)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(201, httpResponse.statusCode());
    }

    @Test
    @Order(3)
    public void userShouldBeAbleToDeleteOwnAccount() throws IOException, InterruptedException {
        String url = host + userEndpoint;
        String userCredentials = "unused@email.com:Password1";
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(userCredentials.getBytes(StandardCharsets.UTF_8));
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Authorization", authHeader)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
    }
}
