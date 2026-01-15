package pl.bankapp.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import pl.bankapp.exception.IncomingTransactionFailedException;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.validator.NipValidator;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

public class CompanyAccountTransferTest {

    private CompanyAccount companyAccount;
    private MockedStatic<NipValidator> mockedValidator;

    @BeforeEach
    void setUp() {
        MockedStatic<NipValidator> mocked = mockStatic(NipValidator.class);
        mocked.when(() -> NipValidator.isNipValid(anyString())).thenReturn(true);
        this.mockedValidator = mocked;
        companyAccount = new CompanyAccount("Alfa", "1234567890");
    }

    @AfterEach
    void tearDown() {
        mockedValidator.close();
    }

    record Transfer(String type, double amount) {}

    @ParameterizedTest(name = "incomingTransfer {0} → expected balance {1}")
    @CsvSource({
            "1000, 1000",
            "2000, 2000"
    })
    void testIncomingTransfer(double transfer, double expectedBalance) {
        double balance = companyAccount.incomingTransfer(transfer);
        assertEquals(expectedBalance, balance);
    }

    @ParameterizedTest(name = "incomingTransfer {0} should throw exception")
    @CsvSource({
            "-1000",
            "-500"
    })
    void testIncomingTransferInvalid(double transfer) {
        Exception exception = assertThrows(
                IncomingTransactionFailedException.class,
                () -> companyAccount.incomingTransfer(transfer)
        );
        assertTrue(exception.getMessage().contains("Wrong value of incoming transfer."));
    }

    @ParameterizedTest(name = "outgoingTransfer {1} from income {0} → expected balance {2}")
    @MethodSource("provideOutgoingTransfers")
    void testOutgoingTransfer(double income, double outgo, double expectedBalance) {
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.outgoingTransfer(outgo);
        assertEquals(expectedBalance, balance);
    }
    static Stream<Arguments> provideOutgoingTransfers() {
        return Stream.of(
                Arguments.of(2000, 1000, 1000),
                Arguments.of(2000, 2000, 0)
        );
    }

    @ParameterizedTest(name = "outgoingTransfer {0} should throw exception")
    @CsvSource({
            "0", "-1000", "5000"
    })
    void testOutgoingTransferInvalid(double outgo) {
        companyAccount.incomingTransfer(1000);
        assertThrows(OutgoingTransactionFailedException.class, () -> companyAccount.outgoingTransfer(outgo));
    }

    @ParameterizedTest(name = "expressOutgoingTransfer {1} from income {0} → expected balance {2}")
    @MethodSource("provideExpressTransfers")
    void testExpressOutgoingTransfer(double income, double outgo, double expectedBalance) {
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.expressOutgoingTransfer(outgo);
        assertEquals(expectedBalance, balance);
    }

    static Stream<Arguments> provideExpressTransfers() {
        return Stream.of(
                Arguments.of(2000, 1000, 995),
                Arguments.of(1000, 1000, -5)
        );
    }

    @ParameterizedTest(name = "sequence of transfers {0} → expected history {1}")
    @MethodSource("provideTransferSequences")
    void testAllTransfersAndHistory(List<Transfer> transfers, List<Double> expectedHistory) {
        for (Transfer t : transfers) {
            switch (t.type()) {
                case "INCOMING" -> companyAccount.incomingTransfer(t.amount());
                case "OUTGOING" -> companyAccount.outgoingTransfer(t.amount());
                case "EXPRESS" -> companyAccount.expressOutgoingTransfer(t.amount());
            }
        }
        assertEquals(expectedHistory, companyAccount.getHistory());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> provideTransferSequences() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(new Transfer("INCOMING", 1000.0)),
                        List.of(1000.0)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(
                                new Transfer("INCOMING", 2000.0),
                                new Transfer("OUTGOING", 1000.0)
                        ),
                        List.of(2000.0, -1000.0)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(
                                new Transfer("INCOMING", 2000.0),
                                new Transfer("OUTGOING", 1000.0),
                                new Transfer("EXPRESS", 5.0)
                        ),
                        List.of(2000.0, -1000.0, -5.0, -5.0)
                )
        );
    }
}
