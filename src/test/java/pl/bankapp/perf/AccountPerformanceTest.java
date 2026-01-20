package pl.bankapp.perf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.entity.TransferType;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountPerformanceTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private static final long MAX_RESPONSE_MS = 500;
    private static final long START = System.currentTimeMillis() % 1_000_000_000L;
    private static final AtomicLong counter = new AtomicLong(START);

    @Test
    public void createAndDeleteAccount100Times() {
        for (int i = 0; i < 100; i++) {
            String pesel = String.format("%011d", counter.getAndIncrement());
            PersonalAccountDTO request = new PersonalAccountDTO("Perf", "User", pesel);

            long startCreate = System.currentTimeMillis();
            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    "/api/accounts",
                    request,
                    String.class
            );
            long durationCreate = System.currentTimeMillis() - startCreate;

            assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
            assertTrue(durationCreate < MAX_RESPONSE_MS, "Create took too long: " + durationCreate + "ms");

            long startDelete = System.currentTimeMillis();
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                    "/api/accounts/" + pesel,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            long durationDelete = System.currentTimeMillis() - startDelete;

            assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
            assertTrue(durationDelete < MAX_RESPONSE_MS, "Delete took too long: " + durationDelete + "ms");
        }
    }

    @Test
    void createAccountAndPerform100IncomingTransfers() {
        String pesel = "12345678901";
        PersonalAccountDTO request = new PersonalAccountDTO("Perf", "Transfer", pesel);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                "/api/accounts",
                request,
                String.class
        );
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        double totalAmount = 0;
        for (int i = 1; i <= 100; i++) {
            TransferDTO transfer = new TransferDTO(i * 10, TransferType.INCOMING);

            long start = System.currentTimeMillis();
            ResponseEntity<String> transferResponse = restTemplate.postForEntity(
                    "/api/accounts/" + pesel + "/transfer",
                    transfer,
                    String.class
            );
            long duration = System.currentTimeMillis() - start;

            assertEquals(HttpStatus.OK, transferResponse.getStatusCode());
            assertTrue(duration < MAX_RESPONSE_MS, "Transfer #" + i + " took too long: " + duration + "ms");

            totalAmount += transfer.getAmount();
        }

        ResponseEntity<PersonalAccountDTO> finalAccount = restTemplate.getForEntity(
                "/api/accounts/" + pesel,
                PersonalAccountDTO.class
        );
        assertNotNull(Objects.requireNonNull(finalAccount.getBody()).pesel);
        assertEquals(pesel, finalAccount.getBody().pesel);
    }
}
