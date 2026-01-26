package pl.bankapp.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bankapp.domain.CompanyAccount;
import pl.bankapp.entity.Account;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.service.SMTPClient;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class AccountEmailTest {

    @Mock
    SMTPClient smtpClient;
    @Captor
    ArgumentCaptor<String> capturedMessage;

    @ParameterizedTest
    @MethodSource
    public void shouldSendHistoryViaEmail(
            Account account,
            boolean smtpResult,
            String expectedText,
            boolean expectedResult
    ) {
        // given
        account.incomingTransfer(100);
        when(smtpClient.send(any(), capturedMessage.capture(), any())).thenReturn(smtpResult);

        // when
        boolean result = account.sendHistoryViaEmail("test@mail.com", smtpClient);
        // then
        assertEquals(expectedResult, result);
        assertEquals(expectedText, capturedMessage.getValue());
    }

    static Stream<Arguments> shouldSendHistoryViaEmail() {
        return Stream.of(
                Arguments.of(
                        new PersonalAccount("John", "DOE", "12345678901"),
                        true,
                        "Personal account history: [100.0]",
                        true
                ),
                Arguments.of(
                        new PersonalAccount("John", "DOE", "12345678901"),
                        false,
                        "Personal account history: [100.0]",
                        false
                ),
                Arguments.of(
                        new CompanyAccount("ABC", "1234567890"),
                        true,
                        "Company account history: [100.0]",
                        true
                ),
                Arguments.of(
                        new CompanyAccount("ABC", "1234567890"),
                        false,
                        "Company account history: [100.0]",
                        false
                )
        );
    }
}


