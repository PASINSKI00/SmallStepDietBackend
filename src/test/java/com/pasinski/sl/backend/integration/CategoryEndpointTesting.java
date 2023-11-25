package com.pasinski.sl.backend.integration;

import com.pasinski.sl.backend.meal.forms.CategoryForm;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryEndpointTesting extends BaseForIntegrationTesting{
    String categoryEndpoint = "/api/category";
    String allCategoriesEndpoint = "/api/category/all";


}
