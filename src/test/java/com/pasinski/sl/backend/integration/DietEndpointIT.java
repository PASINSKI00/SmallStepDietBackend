package com.pasinski.sl.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalDayResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalIngredientResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalMealResponseForm;
import com.pasinski.sl.backend.diet.forms.request.FinalDayModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalIngredientModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalMealModifyRequestForm;
import com.pasinski.sl.backend.user.bodyinfo.Gender;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.Goals;
import com.pasinski.sl.backend.user.forms.UserForm;
import org.json.JSONException;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DietEndpointIT extends BaseForIT {
    static String userEndpoint = "/api/user";
    static String dietUserAuthHeader;
    static String dietUserAuthHeader_2;
    String dietEndpoint = "/api/diet";
    String userBodyInfoEndpoint = "/api/user/bodyinfo";
    static Long dietId;

    @BeforeAll
    static void setup() throws IOException, InterruptedException {
        dietUserAuthHeader = createUser("dietuser@email.com", "Password1");
        dietUserAuthHeader_2 = createUser("dietuser2@email.com", "Password1");
    }

    @BeforeEach
    void before() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(300);
    }

    @AfterAll
    static void cleanup() throws IOException, InterruptedException {
        deleteUser(dietUserAuthHeader);
        deleteUser(dietUserAuthHeader_2);
    }

    @Test
    @Order(1)
    public void authenticatedUser_WithBodyInfoCanPostDiet() throws IOException, InterruptedException {
        //given
        List<List<Long>> days = new ArrayList<>(List.of(new ArrayList<>(Arrays.asList(1L, 2L))));
        String url = host + dietEndpoint;
        postBodyInfo();


        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(days)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(201, httpResponse.statusCode());
        dietId = Long.parseLong(httpResponse.body());
    }

    @Test
    @Order(2)
    public void authenticatedUser_CanRetrieveHisDietById() throws IOException, InterruptedException {
        //given
        String url = host + dietEndpoint + "?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        DietResponseForm createdDietForm = objectMapper.readValue(httpResponse.body(), DietResponseForm.class);
        assertEquals(1, (long) createdDietForm.getFinalDays().size());
        assertEquals(2, (long) createdDietForm.getFinalDays().get(0).getFinalMeals().size());
    }

    @Test
    @Order(3)
    public void authenticatedUser_CanRetrieveHisGroceriesByDietId() throws IOException, InterruptedException {
        //given
        String url = host + dietEndpoint + "/groceries?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
    }

    @Test
    @Order(4)
    public void authenticatedUser_CanRetrieveHisDiets() throws IOException, InterruptedException,
            JSONException {
        //given
        String url = host + dietEndpoint + "/mine";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
    }

    @Test
    @Order(5)
    public void authenticatedUser_CanRetrieveHisUsedUnreviewedMeals() throws IOException, InterruptedException {
        //given
        String url = host + dietEndpoint + "/mine/meals/unreviewed";

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
    }

    @Test
    @Order(6)
    public void authenticatedUser_CanChangeDietMeals() throws IOException, InterruptedException {
        //given
        List<List<Long>> days = new ArrayList<>(List.of(
                new ArrayList<>(Arrays.asList(3L, 4L, 5L)),
                new ArrayList<>(List.of(3L))
                ));
        String url = host + dietEndpoint + "?idDiet=" + dietId;
        postBodyInfo();


        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(days)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
        DietResponseForm createdDietForm = retrieveDiet();
        assertEquals(2,(long) createdDietForm.getFinalDays().size());
        assertEquals(3,(long) createdDietForm.getFinalDays().get(0).getFinalMeals().size());
        assertEquals(1,(long) createdDietForm.getFinalDays().get(1).getFinalMeals().size());
    }

    @Test
    @Order(7)
    public void authenticatedUser_CanModifyFinalDiet() throws IOException, InterruptedException {
        //given
        DietResponseForm dietBefore = retrieveDiet();
        List<FinalDayModifyRequestForm> dayForms = getFinalDayModifyRequestForm(dietBefore);

        //Prep diet form
        FinalDietModifyRequestForm dietForm = new FinalDietModifyRequestForm(dietId,dayForms);
        
        String url = host + dietEndpoint + "/final";
        postBodyInfo();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(dietForm)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());

        DietResponseForm dietAfter = retrieveDiet();
        FinalMealResponseForm firstMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(0);
        FinalMealResponseForm secondMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(1);
        FinalMealResponseForm thirdMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(2);
        assertEquals(20, firstMeal.getPercentOfDay());
        assertEquals(50, secondMeal.getPercentOfDay());
        assertEquals(30, thirdMeal.getPercentOfDay());

        FinalIngredientResponseForm firstIngredient = firstMeal.getFinalIngredients().get(0);
        FinalIngredientResponseForm secondIngredient = firstMeal.getFinalIngredients().get(1);
        assertEquals(3000, firstIngredient.getWeight());
        assertEquals(656, secondIngredient.getWeight());
    }

    @Test
    @Order(8)
    public void authenticatedUser_CanResetDay() throws IOException, InterruptedException {
        //given
        DietResponseForm dietBefore = retrieveDiet();
        Long idFinalDay = dietBefore.getFinalDays().get(0).getIdFinalDay();

        String url = host + dietEndpoint + "/final/day/reset?idDiet=" + dietId + "&idFinalDay=" + idFinalDay;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());

        DietResponseForm dietAfter = retrieveDiet();
        FinalMealResponseForm firstMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(0);
        FinalMealResponseForm secondMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(1);
        FinalMealResponseForm thirdMeal = dietAfter.getFinalDays().get(0).getFinalMeals().get(2);
        assertEquals(33, firstMeal.getPercentOfDay());
        assertEquals(33, secondMeal.getPercentOfDay());
        assertEquals(34, thirdMeal.getPercentOfDay());

        FinalIngredientResponseForm firstIngredient = firstMeal.getFinalIngredients().get(0);
        FinalIngredientResponseForm secondIngredient = firstMeal.getFinalIngredients().get(1);
        assertEquals(262, firstIngredient.getWeight());
        assertEquals(394, secondIngredient.getWeight());
    }

    @Test
    @Order(8)
    public void authenticatedUser_CanReCalculate() throws IOException, InterruptedException {
        //given
        DietResponseForm dietBefore = retrieveDiet();

        String url = host + dietEndpoint + "/final/recalculate?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        DietResponseForm dietAfter = retrieveDiet();
        int caloriesBefore = dietBefore.getFinalDays().get(0).getCalories();
        int caloriesAfter = dietAfter.getFinalDays().get(0).getCalories();

        assertEquals(200, httpResponse.statusCode());
        assertEquals(2853, caloriesAfter);
        assertNotEquals(caloriesBefore, caloriesAfter);
    }

    @Test
    @Order(9)
    public void authenticatedUser_CanNotAccessSomeoneElseDiet() throws IOException, InterruptedException {
        //given
        String url = host + dietEndpoint + "?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader_2)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(403, httpResponse.statusCode());
    }

    @Test
    @Order(10)
    public void authenticatedUser_CanDeleteDiet() throws IOException, InterruptedException {
        //given
        DietResponseForm dietBefore = retrieveDiet();

        String url = host + dietEndpoint + "?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .DELETE()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
        assertThrows(AssertionFailedError.class, this::retrieveDiet);
    }

    @Test
    public void authenticatedUser_WithOutBodyInfoShouldNotBeAbleToPostDiet() throws IOException, InterruptedException {
        //given
        List<List<Long>> days = new ArrayList<>(List.of(new ArrayList<>(Arrays.asList(1L, 2L))));
        String url = host + dietEndpoint;
        deleteBodyInfo();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(days)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(422, httpResponse.statusCode());
    }

    @Test
    public void unauthenticatedUser_WithBodyInfoShouldNotBeAbleToPostDiet() throws IOException, InterruptedException {
        //given
        List<List<Long>> days = new ArrayList<>(List.of(new ArrayList<>(Arrays.asList(1L, 2L))));
        String url = host + dietEndpoint;
        postBodyInfo();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(days)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(401, httpResponse.statusCode());
    }

    private void postBodyInfo() throws IOException, InterruptedException {
        //given
        BodyInfoForm bodyInfoForm = new BodyInfoForm(Goals.MAINTAIN_WEIGHT, 182, 88, 23,
                Gender.MALE, 1.5F, 0);
        String url = host + userBodyInfoEndpoint;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(bodyInfoForm)))
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(201, httpResponse.statusCode());
    }

    private void deleteBodyInfo() throws IOException, InterruptedException {
        //given
        String url = host + userBodyInfoEndpoint;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .DELETE()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertTrue(httpResponse.statusCode() == 200 || httpResponse.statusCode() == 204);
    }

    private DietResponseForm retrieveDiet() throws IOException, InterruptedException {
        //given
        String url = host + dietEndpoint + "?idDiet=" + dietId;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", dietUserAuthHeader)
                .GET()
                .build();

        //when
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        //then
        assertEquals(200, httpResponse.statusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(httpResponse.body(), DietResponseForm.class);
    }

    private static String createUser(String email, String password) throws IOException, InterruptedException {
        UserForm userForm = new UserForm("dietUser", email, password);
        String url = host + userEndpoint;
        String userCredentials = email + ":" + password;
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString(userCredentials.getBytes(StandardCharsets.UTF_8));
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(userForm)))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, httpResponse.statusCode());
        return authHeader;
    }

    private static void deleteUser(String userAuthHeader) throws IOException, InterruptedException {
        String url = host + userEndpoint;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Authorization", userAuthHeader)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, httpResponse.statusCode());
    }

    private static List<FinalDayModifyRequestForm> getFinalDayModifyRequestForm(DietResponseForm dietBefore) {
        FinalDayResponseForm firstDay = dietBefore.getFinalDays().get(0);
        FinalMealResponseForm firstMeal = firstDay.getFinalMeals().get(0);
        FinalMealResponseForm secondMeal = firstDay.getFinalMeals().get(1);
        FinalMealResponseForm thirdMeal = firstDay.getFinalMeals().get(2);

        //Prep ingredient Forms
        long firstIngredientId = firstMeal.getFinalIngredients().get(0).getIdFinalIngredient();
        String firstIngredientName = firstMeal.getFinalIngredients().get(0).getName();
        long secondIngredientId = firstMeal.getFinalIngredients().get(1).getIdFinalIngredient();
        String secondIngredientName = firstMeal.getFinalIngredients().get(1).getName();
        FinalIngredientModifyRequestForm firstIngredientModifyForm =
                new FinalIngredientModifyRequestForm(firstIngredientId,firstIngredientName,3000,null,null);
        FinalIngredientModifyRequestForm secondIngredientRemoveForm =
                new FinalIngredientModifyRequestForm(secondIngredientId,secondIngredientName,null,null,true);
        FinalIngredientModifyRequestForm newIngredientForm =
                new FinalIngredientModifyRequestForm(null,null,656,2L,null);
        List<FinalIngredientModifyRequestForm> ingredientForms =
                new ArrayList<>(List.of(firstIngredientModifyForm,secondIngredientRemoveForm, newIngredientForm));

        //Prep meal forms
        long firstMealId = firstMeal.getIdFinalMeal();
        long secondMealId = secondMeal.getIdFinalMeal();
        long thirdMealId = thirdMeal.getIdFinalMeal();
        FinalMealModifyRequestForm firstMealForm = new FinalMealModifyRequestForm(firstMealId, ingredientForms, 20);
        FinalMealModifyRequestForm secondMealForm = new FinalMealModifyRequestForm(secondMealId, new ArrayList<>(), 50);
        FinalMealModifyRequestForm thirdMealForm = new FinalMealModifyRequestForm(thirdMealId, new ArrayList<>(), 30);
        List<FinalMealModifyRequestForm> mealForms = new ArrayList<>(List.of(firstMealForm, secondMealForm, thirdMealForm));

        //Prep day forms
        long firstDayId = firstDay.getIdFinalDay();
        FinalDayModifyRequestForm dayForm = new FinalDayModifyRequestForm(firstDayId, mealForms);
        return new ArrayList<>(List.of(dayForm));
    }
}
