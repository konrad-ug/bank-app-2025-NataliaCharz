package pl.bankapp.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.bankapp.exception.IncomingTransactionFailedException;
import pl.bankapp.exception.OutgoingTransactionFailedException;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@Tag("unit")
public class CompanyAccountTransferTest {

    private CompanyAccount companyAccount;
    record Transfer(String type, double amount) {}

    @BeforeEach
    public void setUp() {
        companyAccount = new CompanyAccount("ABC", "1234567890");
    }

    @ParameterizedTest
    @MethodSource
    public void testIncomingTransfer(double transfer, double expectedBalance) {
        //given + when
        double balance = companyAccount.incomingTransfer(transfer);
        //then
        assertEquals(expectedBalance, balance);
    }
    static Stream<Arguments> testIncomingTransfer() {
        return Stream.of(
                Arguments.of(1000, 1000),
                Arguments.of(2000, 2000)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testIncomingTransferInvalid(double transfer) {
        //given + when + then
        Exception exception = assertThrows(
                IncomingTransactionFailedException.class,
                () -> companyAccount.incomingTransfer(transfer)
        );
        assertTrue(exception.getMessage().contains("Wrong value of incoming transfer."));
    }
    static Stream<Arguments> testIncomingTransferInvalid() {
        return Stream.of(
                Arguments.of(-1000),
                Arguments.of(-500)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testOutgoingTransfer(double income, double outgo, double expectedBalance) {
        //given + when
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.outgoingTransfer(outgo);
        //then
        assertEquals(expectedBalance, balance);
    }
    static Stream<Arguments> testOutgoingTransfer() {
        return Stream.of(
                Arguments.of(2000, 1000, 1000),
                Arguments.of(2000, 2000, 0)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testOutgoingTransferInvalid(double outgo) {
        companyAccount.incomingTransfer(1000);
        assertThrows(OutgoingTransactionFailedException.class, () -> companyAccount.outgoingTransfer(outgo));
    }
    static Stream<Arguments> testOutgoingTransferInvalid() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1000),
                Arguments.of(5000)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testExpressOutgoingTransfer(double income, double outgo, double expectedBalance) {
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.expressOutgoingTransfer(outgo);
        assertEquals(expectedBalance, balance);
    }
    static Stream<Arguments> testExpressOutgoingTransfer() {
        return Stream.of(
                Arguments.of(2000, 1000, 995),
                Arguments.of(1000, 1000, -5)
        );
    }

    @ParameterizedTest
    @MethodSource
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
    static Stream<Arguments> testAllTransfersAndHistory() {
        return Stream.of(
                Arguments.of(
                        List.of(new Transfer("INCOMING", 1000.0)),
                        List.of(1000.0)
                ),
                Arguments.of(
                        List.of(
                                new Transfer("INCOMING", 2000.0),
                                new Transfer("OUTGOING", 1000.0)
                        ),
                        List.of(2000.0, -1000.0)
                ),
                Arguments.of(
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
