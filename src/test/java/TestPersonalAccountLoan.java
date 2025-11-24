import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestPersonalAccountLoan {

    private PersonalAccount personalAccount;

    @BeforeEach
    public void setUp() {
        String name = "John";
        String surname = "Doe";
        String pesel = "87321930271";
        personalAccount = new PersonalAccount(name, surname, pesel, null);
    }

    private void createBalanceAndHistory(List<Double> amounts) {
        for (Double number : amounts) {
            if (number == null) continue;
            if (number > 0) {
                personalAccount.incomingTransfer(number);
            } else {
                personalAccount.outgoingTransfer(-number);
            }
        }
    }

    @Test
    public void testThreeLastTransactionsAreIncome() {
        //given
        createBalanceAndHistory(List.of(1000.0, 1000.0, 1000.0));
        //when
        boolean lastThree = personalAccount.lastThreeTransactionsAreIncome();
        //then
        assertTrue(lastThree);
    }

    @Test
    public void testThreeLastTransactionsAreNotIncome() {
        //given
        createBalanceAndHistory(List.of(1000.0, -50.0, 1000.0));
        //when
        boolean lastThree = personalAccount.lastThreeTransactionsAreIncome();
        //then
        assertFalse(lastThree);
    }

    @ParameterizedTest
    @MethodSource("provideLastFiveHistories")
    void testLastFiveTransactionsMustBeLargerThanLoan(List<Double> history, double loan, boolean expected) {
        createBalanceAndHistory(history);
        assertEquals(expected, personalAccount.lastFiveTransactionsMustBeLargerThanLoan(loan));
    }

    static Stream<Arguments> provideLastFiveHistories() {
        return Stream.of(
                Arguments.of(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0), 4500.0, true),
                Arguments.of(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0), 5500.0, false),
                Arguments.of(List.of(1000.0, 1000.0, 1000.0, 1000.0), 500.0, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideHistoryForLoan")
    void testSubmitForLoan(List<Double> history, double loan, boolean expected) {
        createBalanceAndHistory(history);
        assertEquals(expected, personalAccount.submitForLoan(loan));
    }

    static Stream<Arguments> provideHistoryForLoan() {
        return Stream.of(
                Arguments.of(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0), 4000.0, true),
                Arguments.of(List.of(1000.0, 1000.0, -1000.0, 1000.0, 1000.0), 6000.0, false),
                Arguments.of(List.of(1000.0, -1000.0, 1000.0, 1000.0, 1000.0), 6000.0, true),
                Arguments.of(List.of(1000.0, 1000.0, 1000.0, -1000.0, 1000.0), 2500.0, true)
        );
    }
}
