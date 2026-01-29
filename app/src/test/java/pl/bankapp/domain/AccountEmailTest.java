package pl.bankapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import pl.bankapp.domain.CompanyAccount;
import pl.bankapp.entity.Account;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.service.NipValidator;
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
    @Mock
    NipValidator nipValidator;
    @InjectMocks
    CompanyAccountService companyAccountService;

    @ParameterizedTest
    @MethodSource
    public void shouldSendHistoryViaEmail(
            String type,
            boolean smtpResult,
            String expectedText,
            boolean expectedResult
    ) {
        // given
        Account account;
        if (type.equals("Personal")) {
            account = new PersonalAccount("John", "DOE", "12345678901");
        } else {
            account = companyAccountService.createCompanyAccount("ABC", "1234567890");
        }
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
                        "Personal",
                        true,
                        "Personal account history: [100.0]",
                        true
                ),
                Arguments.of(
                        "Personal",
                        false,
                        "Personal account history: [100.0]",
                        false
                ),
                Arguments.of(
                        "Company",
                        true,
                        "Company account history: [100.0]",
                        true
                ),
                Arguments.of(
                        "Company",
                        false,
                        "Company account history: [100.0]",
                        false
                )
        );
    }
}


