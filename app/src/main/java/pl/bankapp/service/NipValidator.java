package pl.bankapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class NipValidator {


    private static final String NIP_REGEX = "^[0-9]{10}$";
    HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Feature 18 - walidacja numeru NIP firmy przy zakładaniu konta firmowego
     */
    public void validateNipOrThrow(String nip) {
        if (!isFormatValid(nip)) {
            return;
        }
        if (!isNipActiveInMf(nip)) {
            throw new IllegalArgumentException("Company not registered.");
        }
    }

    /**
     * Feature 7 - walidacja numeru NIP firmy przy zakładaniu konta firmowego, długość 10 cyfr
     */
    private static boolean isFormatValid(String nip) {
        return nip != null && nip.matches(NIP_REGEX);
    }

    /**
     * Feature 18 - walidacja numeru NIP. Stworzy URL do API Ministerstwa Finansów
     */
    private String buildMfUrl(String nip) {
        String base = System.getenv("BANK_APP_MF_URL");
        if (base == null || base.isBlank()) {
            base = "https://wl-test.mf.gov.pl";
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return String.format("%s/api/search/nip/%s?date=%s", base, nip, date);
    }

    /**
     * Feature 18 - walidacja numeru NIP firmy przy zakładaniu konta firmowego
     * Dopiszemy metodę która wyśle zapytanie do API (jako parametr przyjmie NIP) i
     * zwraca True jeżeli odpowiedź zawiera "statusVat": "Czynny", FALSE w innym wypadku.
     */
    private boolean isNipActiveInMf(String nip) {
        String url = buildMfUrl(nip);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("MF response: " + response.body());
            if (response.body() != null && response.body().contains("\"statusVat\":\"Czynny\"")) {
                log.info("NIP {} is active in MF database.", nip);
                return true;
            } else {
                log.warn("NIP {} is not active in MF database.", nip);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error while fetching NIP {} data from MF: {}", nip, e.getMessage(), e);
            throw new RuntimeException("Cannot verify NIP in MF database.", e);
        }
    }
}

