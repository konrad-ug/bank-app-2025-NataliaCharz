package pl.bankapp.cucumber;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class StepDefinitions {

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/accounts";
    }

    private Response response;

    @Given("Account registry is empty")
    public void accountRegistryIsEmpty() {
        Response getAll = RestAssured.get(baseUrl());
        getAll.jsonPath().getList("").forEach(account -> {
            String pesel = ((String)((java.util.Map)account).get("pesel"));
            RestAssured.delete(baseUrl() + "/" + pesel);
        });
    }

    @When("I create an account using name {string} last name {string} pesel {string}")
    public void createAccount(String name, String lastName, String pesel) {
        response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + name + "\",\"surname\":\"" + lastName + "\",\"pesel\":\"" + pesel + "\"}")
                .post(baseUrl());
        assertThat(response.getStatusCode(), is(201));
    }

    @Then("Number of accounts in registry equals {string}")
    public void numberOfAccountsEquals(String count) {
        Response getAll = RestAssured.get(baseUrl());
        assertThat(getAll.jsonPath().getList("").size(), is(Integer.parseInt(count)));
    }

    @Then("Account with pesel {string} exists in registry")
    public void accountWithPeselExists(String pesel) {
        Response get = RestAssured.get(baseUrl() + "/" + pesel);
        assertThat(get.getStatusCode(), is(200));
    }

    @Then("Account with pesel {string} does not exist in registry")
    public void accountWithPeselDoesNotExist(String pesel) {
        Response get = RestAssured.get(baseUrl() + "/" + pesel);
        assertThat(get.getStatusCode(), is(404));
    }

    @When("I delete account with pesel {string}")
    public void deleteAccount(String pesel) {
        RestAssured.delete(baseUrl() + "/" + pesel);
    }

    @When("I update {string} of account with pesel {string} to {string}")
    public void updateField(String field, String pesel, String value) {
        if (!field.equals("name") && !field.equals("surname")) {
            throw new IllegalArgumentException("Field must be 'name' or 'surname'");
        }
        RestAssured.given()
                .header("Content-Type", "application/json")
                .body("{\"" + field + "\":\"" + value + "\"}")
                .patch(baseUrl() + "/" + pesel)
                .then()
                .statusCode(200);
    }

    @Then("Account with pesel {string} has {string} equal to {string}")
    public void fieldEquals(String pesel, String field, String value) {
        Response get = RestAssured.get(baseUrl() + "/" + pesel);
        assertThat(get.jsonPath().getString(field), is(value));
    }
}

