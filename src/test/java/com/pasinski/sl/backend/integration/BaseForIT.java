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
        String userCredentials = "email@email.com:Password1";
        userAuthorizationHeader = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes(StandardCharsets.UTF_8));
        String adminCredentials = "admin@email.com:Password1";
        adminAuthorizationHeader = "Basic " + Base64.getEncoder().encodeToString(adminCredentials.getBytes(StandardCharsets.UTF_8));
    }
}
