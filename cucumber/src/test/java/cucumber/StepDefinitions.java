package cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.entity.TransferType;

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
            String pesel = ((String) ((java.util.Map<?, ?>) account).get("pesel"));
            RestAssured.delete(baseUrl() + "/" + pesel);
        });
    }

    @When("I create an account using name {string} last name {string} pesel {string}")
    public void createAccount(String name, String lastName, String pesel) {
        response = RestAssured.given()
                .contentType("application/json")
                .body("""
                        {
                          "name": "%s",
                          "surname": "%s",
                          "pesel": "%s"
                        }
                        """.formatted(name, lastName, pesel))
                .post(baseUrl());
        assertThat(response.getStatusCode(), is(201));
    }

    @When("I delete account with pesel {string}")
    public void deleteAccount(String pesel) {
        RestAssured.delete(baseUrl() + "/" + pesel)
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @When(
            "I update {string} of account with pesel {string} to {string}"
    )
    public void updateField(String field, String pesel, String value) {
        RestAssured.given()
                .contentType("application/json")
                .body("""
                        {
                          "%s": "%s"
                        }
                        """.formatted(field, value))
                .patch(baseUrl() + "/" + pesel)
                .then()
                .statusCode(200);
    }

    @When("I transfer {double} from account with pesel {string} as {string}")
    public void makeTransfer(double amount, String pesel, String type) {
        TransferDTO transferDTO = new TransferDTO(amount, TransferType.valueOf(type.toUpperCase()));

        response = RestAssured.given()
                .contentType("application/json")
                .body(transferDTO)
                .post(baseUrl() + "/" + pesel + "/transfer");
    }

    @When("I transfer {double} to account with pesel {string}")
    public void iTransferToAccountWithPesel(double amount, String pesel){
        TransferDTO transferDTO = new TransferDTO(amount, TransferType.INCOMING);

        response = RestAssured.given()
                .contentType("application/json")
                .body(transferDTO)
                .post(baseUrl() + "/" + pesel + "/transfer");
    }

    @Then("Number of accounts in registry equals {string}")
    public void numberOfAccountsEquals(String count) {
        Response getAll = RestAssured.get(baseUrl());
        assertThat(
                getAll.jsonPath().getList("").size(),
                is(Integer.parseInt(count))
        );
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

    @Then("Account with pesel {string} has {string} equal to {string}")
    public void fieldEquals(String pesel, String field, String value) {
        Response get = RestAssured.get(baseUrl() + "/" + pesel);
        assertThat(get.jsonPath().getString(field), is(value));
    }

    @Then("Account with pesel {string} has the following fields correctly set")
    public void account_with_pesel_has_the_following_fields_correctly_set(String pesel) {
        Response get = RestAssured.get(baseUrl() + "/" + pesel);
        assertThat(get.getStatusCode(), is(200));

        assertThat(get.jsonPath().getString("name"), is("jan"));
        assertThat(get.jsonPath().getString("surname"), is("kowalski"));
        assertThat(get.jsonPath().getString("pesel"), is(pesel));
    }

    @Then("The transfer is successful")
    public void transferIsSuccessful() {
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.getBody().asString(), containsString("The order has been accepted for execution"));
    }

    @Then("The transfer fails with status {int}")
    public void transferFailsWithStatus(int statusCode) {
        assertThat(response.getStatusCode(), is(statusCode));
    }
}

