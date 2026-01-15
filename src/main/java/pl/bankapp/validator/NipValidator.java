package pl.bankapp.validator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class NipValidator {

    private static final String NIP_REGEX = "^[0-9]{10}$";

    private NipValidator(){
    }

    public static String validateNip(String nip) {
        if (nip == null || !Pattern.matches(NIP_REGEX, nip)) {
            return null;
        } else {
            return nip;
        }
    }

    public static boolean isNipActiveInMf(String nip) {
        String base = System.getenv("BANK_APP_MF_URL");
        if (base == null || base.isBlank()) {
            base = "https://wl-test.mf.gov.pl";
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String url = String.format("%s/api/search/nip/%s?date=%s", base, nip, date);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            System.out.println("MF response: " + body);
            return body != null && body.contains("\"statusVat\":\"Czynny\"");
        } catch (IOException | InterruptedException e) {
            System.out.println("Error calling MF API: " + e.getMessage());
            return false;
        }
    }
}
