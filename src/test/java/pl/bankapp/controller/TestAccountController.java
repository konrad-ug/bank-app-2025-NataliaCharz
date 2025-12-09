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
import pl.bankapp.entity.TransferRequest;
import pl.bankapp.entity.TransferType;
import pl.bankapp.service.AccountsRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
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
        PersonalAccount person1 = new PersonalAccount("Natalia", "Charz", "12345678909");
        PersonalAccount person2 = new PersonalAccount("Adam", "Małysz", "98657643212");
        accountsRegistry.addAccount(person1);
        accountsRegistry.addAccount(person2);
        person1.incomingTransfer(150.0);
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
    public void shouldReturn409WhenCreatingAccountWithAlreadyExistedPesel(){
        //given
        PersonalAccount request = new PersonalAccount("Wojtek", "Szczęsny", "12345678909");
        //when
        RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    public void shouldReturnAllAccounts(){
        //given + when
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
        assertTrue(pesels.contains("12345678909"));
        assertTrue(pesels.contains("98657643212"));
    }

    @Test
    public void shouldReturnEmptyListWhenNothingInAccountRegistry(){
        //given
        accountsRegistry.getAllAccounts().clear();
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
        //given
        accountsRegistry.getAllAccounts().clear();
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
        //given + when
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
        String pesel = "12345678909";
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
        PersonalAccount toChange = new PersonalAccount("Ania", "Charz", "12345678909");
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
        PersonalAccount toChange = new PersonalAccount("Ania", "Charz", "12344678909");
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
    public void shouldReturn404WhenInvalidPesel(){
        //given
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
    public void shouldDeletePersonalAccountIfPeselExists(){
        //given
        String expected = "12345678909";
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
        String expected = "11111111111";
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
    public void shouldReturn404WhenPatchingNonExistingPesel() {
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

    @Test
    public void shouldReturn200WhenIncomingTransferSucceeds(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(100.0, TransferType.INCOMING);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturn200WhenOutgoingTransferSucceeds(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(10.0, TransferType.OUTGOING);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturn200WhenExpressTransferSucceeds(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(100.0, TransferType.EXPRESS);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void shouldReturnCorrectSuccessMessageOnSuccessfulTransfer(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(100.0, TransferType.INCOMING);
        //when
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("The order has been accepted for execution"));
    }

    @Test
    public void shouldReturn404WhenAccountDoesNotExist(){
        //given
        String pesel = "99999999999";
        TransferRequest transferRequest = new TransferRequest(100.0, TransferType.EXPRESS);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldReturn400WhenTransferTypeIsNull(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(100.0, null);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void shouldReturn400WhenAmountIsNegative(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(-100.0, TransferType.OUTGOING);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    @Test
    public void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs(){
        //given
        String pesel = "12345678909";
        TransferRequest transferRequest = new TransferRequest(-100.0, TransferType.INCOMING);
        //when + then
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferRequest)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
