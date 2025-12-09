package pl.bankapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.bankapp.BankApplication;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.service.AccountsRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bankapp.controller.AccountController.ACCOUNT_URL;

@SpringBootTest(classes = BankApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class TestAccountController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountsRegistry accountsRegistry;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accountsRegistry.getAllAccounts().clear();
    }

    @Test
    public void shouldCreatePersonalBankAccountWhenValidRequest(){
        //given
        PersonalAccount request = new PersonalAccount("Adam", "Sandler", "12345678912");
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        //then
        PersonalAccount returnedObject = null;
        try {
            returnedObject = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.statusCode());
        assertEquals("Adam", returnedObject.getName());
        assertEquals("Sandler", returnedObject.getSurname());
        assertEquals("12345678912", returnedObject.getIdentification());
    }

    @Test
    public void shouldReturnConflictWhenCreatingAccountWithAlreadyExistedPesel(){
        //given
        PersonalAccount request = new PersonalAccount("Adam", "Sandler", "12345678912");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        PersonalAccount request2 = new PersonalAccount("Wojtek", "Szczęsny", "12345678912");
        //when
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request2)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void shouldReturnAllAccounts(){
        //given
        PersonalAccount request1 = new PersonalAccount("Adam", "Sandler", "12345678912");
        PersonalAccount request2 = new PersonalAccount("Adam", "Małysz", "98657643212");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request1)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request2)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL)
                .then()
                .extract().response();
        //then
        PersonalAccount[] accountsArray = null;
        try {
            accountsArray = objectMapper.readValue(response.getBody().asString(), PersonalAccount[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<PersonalAccount> accounts = Arrays.asList(accountsArray);
        assertTrue(accounts.size() >= 2);

        List<String> pesels = accounts.stream()
                .map(PersonalAccount::getIdentification)
                .toList();
        assertTrue(pesels.contains("12345678912"));
        assertTrue(pesels.contains("98657643212"));
    }

    @Test
    public void shouldReturnEmptyListWhenNothingInAccountRegistry(){
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL)
                .then()
                .extract().response();
        List<PersonalAccount> mappedResponse = null;
        try {
            mappedResponse = objectMapper.readValue(response.getBody().asString(), List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //then
        assertEquals(List.of(), mappedResponse);
    }

    @Test
    public void shouldReturnZeroWhenNothingInAccountRegistry(){
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/count")
                .then()
                .extract().response();
        Map<String, Integer> mappedResponse = null;
        try {
            mappedResponse = objectMapper.readValue(response.getBody().asString(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //then
        assertEquals(0, mappedResponse.get("count"));
    }

    @Test
    public void shouldReturnNumberOfAccountsIfListNotEmpty(){
        //given
        PersonalAccount request1 = new PersonalAccount("Adam", "Sandler", "12345678912");
        PersonalAccount request2 = new PersonalAccount("Adam", "Małysz", "98657643212");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request1)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request2)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/count")
                .then()
                .extract().response();
        Map<String, Integer> mappedResponse = null;
        try {
             mappedResponse = objectMapper.readValue(response.getBody().asString(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //then
        assertEquals(2, mappedResponse.get("count"));
    }

    @Test
    public void shouldReturnAccountWhenValidPesel(){
        //given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        String pesel = "12345678909";
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL);
        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/" + pesel)
                .then()
                .extract().response();
        PersonalAccount mappedResponse = null;
        try {
            mappedResponse = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //then
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Natalia", mappedResponse.getName());
        assertEquals("Charz", mappedResponse.getSurname());

    }
    @Test
    public void shouldUpdatePersonalAccountWhenValidPesel(){
        //given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        PersonalAccount toChange = new PersonalAccount("Ania", "Charz", "12345678909");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        // when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(toChange)
                .when()
                .put(ACCOUNT_URL + "/" + toChange.getIdentification())
                .then()
                .extract().response();
        //then
        PersonalAccount returnedObject = null;
        try {
            returnedObject = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.statusCode());
        assertEquals("Ania", returnedObject.getName());
        assertEquals("Charz", returnedObject.getSurname());
        assertEquals("12345678909", returnedObject.getIdentification());
    }

    @Test
    public void shouldNotUpdatePersonalAccountWhenInValidPesel(){
        //given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        PersonalAccount toChange = new PersonalAccount("Ania", "Charz", "12344678909");

        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        // when + then
       RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(toChange)
                .when()
                .put(ACCOUNT_URL + "/" + toChange.getIdentification())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    public void shouldReturnNotFoundWhenInvalidPesel(){
        //given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        String pesel = "84620192781";
        //when
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/" + pesel)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @Test
    public void shouldDeletePersonalAccountIfPeselExistsInAccountRegistry(){
        //given
        PersonalAccount request = new PersonalAccount("Adam", "Sandler", "12345678912");
        String expected = "12345678912";
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        //when && then
        RestAssured.given()
                .when()
                .delete(ACCOUNT_URL + "/" + expected)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldNotDeletePersonalAccountIfPeselInvalid(){
        //given
        PersonalAccount request = new PersonalAccount("Adam", "Sandler", "12345678912");
        String expected = "11111111111";
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();
        //when && then
        RestAssured.given()
                .when()
                .delete(ACCOUNT_URL + "/" + expected)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldPartialUpdateNameWhenOneArgProvided() {
        // given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();

        String pesel = "12345678909";
        String[] args = new String[] { "Ania" };
        String body;
        try {
            body = objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(body)
                .when()
                .patch(ACCOUNT_URL + "/" + pesel)
                .then()
                .extract().response();

        // then
        PersonalAccount returned = null;
        try {
            returned = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Ania", returned.getName());
        assertEquals("Charz", returned.getSurname());
        assertEquals("12345678909", returned.getIdentification());
    }

    @Test
    public void shouldPartialUpdateSurnameWhenSecondArgProvided() {
        // given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();

        String pesel = "12345678909";
        Object[] args = new Object[] { null, "Nowak" };
        String body;
        try {
            body = objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(body)
                .when()
                .patch(ACCOUNT_URL + "/" + pesel)
                .then()
                .extract().response();

        // then
        PersonalAccount returned = null;
        try {
            returned = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Natalia", returned.getName());
        assertEquals("Nowak", returned.getSurname());
        assertEquals("12345678909", returned.getIdentification());
    }

    @Test
    public void shouldPartialUpdateBothNameAndSurnameWhenTwoArgsProvided() {
        // given
        PersonalAccount req = new PersonalAccount("Natalia", "Charz", "12345678909");
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(req)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .extract().response();

        String pesel = "12345678909";
        String[] args = new String[] { "Kasia", "Kowalska" };
        String body;
        try {
            body = objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(body)
                .when()
                .patch(ACCOUNT_URL + "/" + pesel)
                .then()
                .extract().response();

        // then
        PersonalAccount returned = null;
        try {
            returned = objectMapper.readValue(response.getBody().asString(), PersonalAccount.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Kasia", returned.getName());
        assertEquals("Kowalska", returned.getSurname());
        assertEquals("12345678909", returned.getIdentification());
    }

    @Test
    public void shouldReturnNotFoundWhenPatchingNonExistingPesel() {
        // given
        String pesel = "00000000000";
        String[] args = new String[] { "X" };
        String body;
        try {
            body = objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(body)
                .when()
                .patch(ACCOUNT_URL + "/" + pesel)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

}
