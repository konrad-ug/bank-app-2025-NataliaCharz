package pl.bankapp.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import pl.bankapp.validator.NipValidator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class CompanyAccountTest {

    private CompanyAccount companyAccount;

    @ParameterizedTest
    @MethodSource("invalidNips")
    void shouldCreateAccountWhenNipHasInvalidFormat(String nip) {
        CompanyAccount account = new CompanyAccount("ABC", nip);
        assertNotNull(account);
    }
    static Stream<String> invalidNips() {
        return Stream.of(
                "123",
                "12345678901123321",
                "ABC123XYZ",
                null
        );
    }

    @Test
    void shouldThrowExceptionWhenNipNotActiveInMf() {
        try (MockedStatic<NipValidator> mocked = mockStatic(NipValidator.class)) {
            mocked.when(() -> NipValidator.validateNipOrThrow("1234567890"))
                    .thenThrow(new IllegalArgumentException("Company not registered."));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new CompanyAccount("ABC", "1234567890")
            );
            assertEquals("Company not registered.", ex.getMessage());
        }
    }

    @Test
    void shouldCreateAccountWhenNipIsValidAndActive() {
        try (MockedStatic<NipValidator> mocked = mockStatic(NipValidator.class)) {
            mocked.when(() -> NipValidator.validateNipOrThrow("8461627563"))
                    .thenAnswer(invocation -> null);
            CompanyAccount account = new CompanyAccount("ABC", "8461627563");
            assertNotNull(account);
        }
    }

}

