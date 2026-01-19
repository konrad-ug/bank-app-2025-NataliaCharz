package pl.bankapp.entity;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.bankapp.service.SMTPClient;
import pl.bankapp.validator.NipValidator;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class AccountEmailTest {

    @ParameterizedTest
    @MethodSource("accountProvider")
    void shouldSendHistoryViaEmailParameterized(
            Account account,
            boolean smtpResult,
            String expectedText,
            boolean expectedResult
    ) {
        // given
        account.incomingTransfer(100);
        try (MockedConstruction<SMTPClient> smtpMock =
                     Mockito.mockConstruction(SMTPClient.class,
                             (mock, context) ->
                                     Mockito.when(
                                             mock.send(
                                                     Mockito.anyString(),
                                                     Mockito.anyString(),
                                                     Mockito.anyString()
                                             )
                                     ).thenReturn(smtpResult)
                     )) {

            // when
            boolean result = account.sendHistoryViaEmail("test@mail.com");
            // then
            assertEquals(expectedResult, result);
            SMTPClient client = smtpMock.constructed().get(0);
            Mockito.verify(client).send(
                    Mockito.contains("Account Transfer History"),
                    Mockito.contains(expectedText),
                    Mockito.eq("test@mail.com")
            );
        }
    }

    static Stream<Arguments> accountProvider() {
        try (
                MockedStatic<NipValidator> nipMock = Mockito.mockStatic(NipValidator.class);
        ) {
            nipMock.when(() -> NipValidator.validateNipOrThrow(Mockito.any()))
                    .thenAnswer(invocation -> null);
            return Stream.of(
                    Arguments.of(
                            new PersonalAccount("John", "DOE", "12345678901"),
                            true,
                            "Personal account history",
                            true
                    ),
                    Arguments.of(
                            new PersonalAccount("John", "DOE", "12345678901"),
                            false,
                            "Personal account history",
                            false
                    ),
                    Arguments.of(
                            new CompanyAccount("ABC", "1234567890"),
                            true,
                            "Company account history",
                            true
                    ),
                    Arguments.of(
                            new CompanyAccount("ABC", "1234567890"),
                            false,
                            "Company account history",
                            false
                    )
            );
        }
    }
}

