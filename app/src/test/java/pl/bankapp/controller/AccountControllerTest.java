package pl.bankapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.dto.PersonalAccountPartialUpdateDTO;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.entity.TransferType;
import pl.bankapp.service.AccountsRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.bankapp.controller.AccountController.ACCOUNT_URL;

@Tag("api")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTest {

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
        person1.incomingTransfer(150.0);
        PersonalAccount person2 = new PersonalAccount("Adam", "Małysz", "98657643212");
        accountsRegistry.addAccount(person1);
        accountsRegistry.addAccount(person2);
    }


    @ParameterizedTest
    @MethodSource
    public void shouldReturnGivenHttpStatusWhenCreatePersonalAccount(PersonalAccountDTO request, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .post(ACCOUNT_URL)
                .then()
                .statusCode(expectedStatus.value());

        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream<Arguments> shouldReturnGivenHttpStatusWhenCreatePersonalAccount(){
        return Stream.of(
                Arguments.of(new PersonalAccountDTO("John", "Doe", "11223344556"), HttpStatus.CREATED),
                Arguments.of(new PersonalAccountDTO("Alice", "Johnson", "99887766554"), HttpStatus.CREATED),
                Arguments.of(new PersonalAccountDTO("Bob", "Brown", "12345678909"), HttpStatus.CONFLICT),
                Arguments.of(new PersonalAccountDTO("Alan", "Smith", null), HttpStatus.BAD_REQUEST),
                Arguments.of(new PersonalAccountDTO("Eve", "Davis", "abcde12345"), HttpStatus.BAD_REQUEST)
        );
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
        PersonalAccount[] accountsArray;
        try {
            accountsArray = objectMapper.readValue(response.getBody().asString(), PersonalAccount[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        List<PersonalAccount> accounts = Arrays.asList(accountsArray);
        assertTrue(accounts.size() >= 2);
    }

    @Test
    public void shouldReturnEmptyListWhenNothingInAccountRegistry() throws JsonProcessingException {
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
        List<PersonalAccount> mappedResponse = objectMapper.readValue(response.getBody().asString(), List.class);

        //then
        assertEquals(List.of(), mappedResponse);
    }

    @Test
    public void shouldReturnZeroWhenNothingInAccountRegistry() throws JsonProcessingException {
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
        Map<String, Integer> mappedResponse = objectMapper.readValue(response.getBody().asString(), Map.class);
        //then
        assertEquals(0, mappedResponse.get("count"));
    }

    @Test
    public void shouldReturnNumberOfAccountsIfListNotEmpty() throws JsonProcessingException {
        //given + when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/count")
                .then()
                .extract().response();
        Map<String, Integer> mappedResponse = objectMapper.readValue(response.getBody().asString(), Map.class);
        //then
        assertEquals(2, mappedResponse.get("count"));
    }

    @ParameterizedTest
    @MethodSource()
    public void shouldReturnExpectedHttpStatusWhenGetPersonalAccountByPesel(String pesel, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .when()
                .get(ACCOUNT_URL + "/" + pesel)
                .then()
                .statusCode(expectedStatus.value());
        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream <Arguments> shouldReturnExpectedHttpStatusWhenGetPersonalAccountByPesel(){
        return Stream.of(
                Arguments.of("12345678909", HttpStatus.OK),
                Arguments.of("98657643212", HttpStatus.OK),
                Arguments.of("00000000000", HttpStatus.NOT_FOUND)
        );
    }


    @ParameterizedTest
    @MethodSource
    public void shouldReturnExpectedHttpStatusWhenUpdatePersonalAccount(PersonalAccountDTO request, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .put(ACCOUNT_URL + "/" + request.getPesel())
                .then()
                .statusCode(expectedStatus.value());
        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream<Arguments> shouldReturnExpectedHttpStatusWhenUpdatePersonalAccount(){
        return Stream.of(
                Arguments.of(new PersonalAccountDTO("John", "Doe", "12345678909"), HttpStatus.OK),
                Arguments.of(new PersonalAccountDTO("John", "Doe", "11223344556"), HttpStatus.NOT_FOUND)
        );
    }


    @ParameterizedTest
    @MethodSource
    public void shouldReturnExpectedHttpStatusWhenDeletePersonalAccount(String request, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .when()
                .delete(ACCOUNT_URL + "/" + request)
                .then()
                .statusCode(expectedStatus.value());
        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream<Arguments> shouldReturnExpectedHttpStatusWhenDeletePersonalAccount(){
        return Stream.of(
                Arguments.of("12345678909", HttpStatus.OK),
                Arguments.of("11111111111", HttpStatus.NOT_FOUND)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void shouldReturnExpectedHttpStatusWhenPartialUpdatePersonalAccount(PersonalAccountPartialUpdateDTO request, String pesel, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .and()
                .body(request)
                .when()
                .patch(ACCOUNT_URL + "/" + pesel)
                .then()
                .statusCode(expectedStatus.value());
        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream<Arguments> shouldReturnExpectedHttpStatusWhenPartialUpdatePersonalAccount(){
        return Stream.of(
                Arguments.of(new PersonalAccountPartialUpdateDTO("Anna", null), "12345678909", HttpStatus.OK),
                Arguments.of(new PersonalAccountPartialUpdateDTO(null, "Kowalska"), "12345678909", HttpStatus.OK),
                Arguments.of(new PersonalAccountPartialUpdateDTO("Kasia", "Nowak"), "12345678909", HttpStatus.OK),
                Arguments.of(new PersonalAccountPartialUpdateDTO(null, null), "00000000000", HttpStatus.NOT_FOUND)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void shouldProcessTransfer(String pesel, TransferDTO transferDTO, HttpStatus expectedStatus){
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(transferDTO)
                .when()
                .post(ACCOUNT_URL + "/" + pesel + "/transfer")
                .then()
                .statusCode(expectedStatus.value());
        //then
        assertEquals(expectedStatus.value(), response.extract().statusCode());
    }

    static Stream<Arguments> shouldProcessTransfer() {
        return Stream.of(
                Arguments.of("12345678909", new TransferDTO(100.0, TransferType.INCOMING), HttpStatus.OK),
                Arguments.of("12345678909", new TransferDTO(10.0, TransferType.OUTGOING), HttpStatus.OK),
                Arguments.of("12345678909", new TransferDTO(100.0, TransferType.EXPRESS), HttpStatus.OK),
                Arguments.of("11111111111", new TransferDTO(100.0, TransferType.EXPRESS), HttpStatus.NOT_FOUND),
                Arguments.of("12345678909", new TransferDTO(50.0, null), HttpStatus.BAD_REQUEST),
                Arguments.of("12345678909", new TransferDTO(-100.0, TransferType.OUTGOING), HttpStatus.UNPROCESSABLE_ENTITY)
        );
    }
}
