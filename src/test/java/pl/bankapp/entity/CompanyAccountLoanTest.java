package pl.bankapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
public class CompanyAccountLoanTest {

    private CompanyAccount companyAccount;

    @BeforeEach
    public void setUp(){
        String name = "ABC";
        String nip = "12345678901";
        companyAccount = new CompanyAccount(name, nip);
    }

    private void createBalanceAndHistory(List<Double> amounts){
        for (Double number : amounts) {
            if (number == null) continue;
            if (number > 0) {
                companyAccount.incomingTransfer(number);
            } else {
                companyAccount.outgoingTransfer(-number);
            }
        }
    }

    @ParameterizedTest
    @MethodSource
    public void testCompanyAccountSubmitForLoan(List<Double> history, double loan, boolean expected){
        createBalanceAndHistory(history);
        assertEquals(expected, companyAccount.submitForLoan(loan));
    }
    static Stream<Arguments> testCompanyAccountSubmitForLoan(){
        return Stream.of(
                Arguments.of(List.of(2000.0, -1775.0, 5000.0, 5000.0), 5000.0, true),
                Arguments.of(List.of(2000.0, 1775.0, 5000.0, 5000.0), 5000.0, false),
                Arguments.of(List.of(2000.0, -1775.0, 1000.0, 5000.0), 5000.0, false)
        );

    }
}
