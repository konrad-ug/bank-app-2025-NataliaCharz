package pl.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("unit")
public class NipValidatorServiceTest {
    private NipValidator nipValidator;
    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    void setUp() {
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);

        nipValidator = new NipValidator() {
            {
                this.httpClient = httpClientMock;
            }
        };
    }

    @Test
    void validateNipOrThrow_shouldNotThrowForInvalidFormat() {
        assertDoesNotThrow(() -> nipValidator.validateNipOrThrow("123")); // złe dane
    }

    @Test
    void validateNipOrThrow_shouldThrowIfInactiveInMf() throws Exception {
        when(httpResponseMock.body()).thenReturn("{\"statusVat\":\"Nieczynny\"}");
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> nipValidator.validateNipOrThrow("1234567890"));
        assertEquals("Company not registered.", ex.getMessage());
    }

    @Test
    void validateNipOrThrow_shouldNotThrowIfActiveInMf() throws Exception {
        when(httpResponseMock.body()).thenReturn("{\"statusVat\":\"Czynny\"}");
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponseMock);

        assertDoesNotThrow(() -> nipValidator.validateNipOrThrow("1234567890"));
    }
}
