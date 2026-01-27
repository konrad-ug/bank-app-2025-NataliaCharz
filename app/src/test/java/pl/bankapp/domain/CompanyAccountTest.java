package pl.bankapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import pl.bankapp.service.NipValidator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
public class CompanyAccountTest {

    @Mock
    NipValidator nipValidator;

    @InjectMocks
    CompanyAccountService companyAccountService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @MethodSource
    public void shouldCreateAccountWhenNipHasInvalidFormat(String nip) {
        CompanyAccount account = new CompanyAccount("ABC", nip);
        assertNotNull(account);
    }
    static Stream<String> shouldCreateAccountWhenNipHasInvalidFormat() {
        return Stream.of(
                "123",
                "12345678901123321",
                "ABC123XYZ",
                null
        );
    }

    @Test
    void shouldThrowWhenNipNotActive() {
        doThrow(new IllegalArgumentException("Company not registered."))
                .when(nipValidator).validateNipOrThrow("1234567890");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> companyAccountService.createCompanyAccount("ABC", "1234567890")
        );

        assertEquals("Company not registered.", ex.getMessage());
    }

    @Test
    void shouldCreateWhenNipValid() {
        doNothing().when(nipValidator).validateNipOrThrow("8461627563");

        CompanyAccount account = companyAccountService.createCompanyAccount("ABC", "8461627563");

        assertNotNull(account);
    }
}

