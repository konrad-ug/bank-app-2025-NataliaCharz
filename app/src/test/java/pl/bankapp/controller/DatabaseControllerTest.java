package pl.bankapp.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.service.AccountsRegistry;
import pl.bankapp.service.PersonalAccountService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("api")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DatabaseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PersonalAccountService personalAccountService;

    @Autowired
    private AccountsRegistry accountsRegistry;

    private static final String DATABASE_URL = "/api/database";

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

    @Test
    void dumpRegistryToDatabase_shouldReturnOk() {
        //given + when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .post(DATABASE_URL + "/dump")
                .then()
                .statusCode(HttpStatus.OK.value());

        //then
        assertEquals(HttpStatus.OK.value(), response.extract().statusCode());
        List<PersonalAccount> dbAccounts = personalAccountService.loadAccountsFromDatabase();
        assertTrue(dbAccounts.size() >= 2);
    }

    @Test
    void loadDatabaseToRegistry_shouldPopulateRegistry() {
        personalAccountService.dumpAccountsToDatabase(accountsRegistry.getAllAccounts());
        accountsRegistry.getAllAccounts().clear();

        //when
        ValidatableResponse response = RestAssured.given()
                .header("Content-Type", "application/json")
                .when()
                .post(DATABASE_URL + "/load")
                .then()
                .statusCode(HttpStatus.OK.value());

        //then
        assertEquals(HttpStatus.OK.value(), response.extract().statusCode());
        assertTrue(accountsRegistry.getAllAccounts().size() >= 2);
    }

    @Test
    void loadDatabaseToRegistry_shouldReturnEmptyIfNoData() {
        accountsRegistry.getAllAccounts().clear();
        personalAccountService.dumpAccountsToDatabase(List.of());

        //when
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .post(DATABASE_URL + "/load")
                .then()
                .extract().response();

        List<?> mappedResponse = accountsRegistry.getAllAccounts();
        assertTrue(mappedResponse.isEmpty());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode());
    }
}

