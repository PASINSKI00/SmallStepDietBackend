package com.pasinski.sl.backend.integration;

import com.google.gson.Gson;
import com.pasinski.sl.backend.user.forms.UserForm;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseForIT{
    protected static String userAuthorizationHeader;
    protected static String adminAuthorizationHeader;
    static String host = "http://localhost:8080";
    static HttpClient httpClient = HttpClient.newHttpClient();
    static Gson gson = new Gson();

    @BeforeAll
    public static void setUpUsers() throws IOException, InterruptedException {
        userAuthorizationHeader = createUser("Charlie", "email@email.com", "Password1");
        String adminCredentials = "admin@email.com:Password1";
        adminAuthorizationHeader = "Basic " + Base64.getEncoder().encodeToString(adminCredentials.getBytes(StandardCharsets.UTF_8));
    }

    @AfterAll
    public static void deleteUsers() throws IOException, InterruptedException {
        deleteUser(userAuthorizationHeader);
    }

    private static String createUser(String name, String email, String password) throws IOException, InterruptedException {
        UserForm userForm = new UserForm(name, email, password);
        String url = host + "/api/user";
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(userForm)))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, httpResponse.statusCode());
        String credentials = email + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    private static void deleteUser(String authorizationHeader) throws IOException, InterruptedException {
        String url = host + "/api/user";
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Authorization", authorizationHeader)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
    }
}
