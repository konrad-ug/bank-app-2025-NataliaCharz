import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestCompanyAccountBalance {

    private CompanyAccount companyAccount;

    @BeforeEach
    public void setUp(){
        String companyName = "Alfa";
        String nip = "12345678901";
        companyAccount = new CompanyAccount(companyName, nip);
    }
    @Test
    public void testCompanyAccountAddProperAmountToBalanceIncomingTransfer() {
        //given
        double transfer = 1000.0;
        //when
        Double balance = companyAccount.incomingTransfer(transfer);
        //then
        assertEquals(1000.0, balance);
    }

    @Test
    public void testCompanyAccountThrowExceptionWhenInvalidAmountIncomingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            companyAccount.incomingTransfer(-1000);
        });
        String expectedMessage = "Wrong value of incoming transfer.";
        String actualMessage = exception.getMessage();
        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testCompanyAccountGetBalanceAfterOutgoingTransfer() {
        //given
        double income = 2000;
        double outgo = 1000;
        //when
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.outgoingTransfer(outgo);
        //then
        assertEquals(1000.0, balance);
    }

    @Test
    public void testCompanyAccountThrowExceptionWhenOutgoingTransferValueHigherThanBalance(){
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            companyAccount.outgoingTransfer(1000);
        });
        String expectedMessage = "Balance is lower than outgo";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testCompanyAccountExpressOutgoingTransfer(){
        //given
        double income = 2000;
        double outgo = 1000;
        //when
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.expressOutgoingTransfer(outgo);
        //then
        assertEquals(995, balance);
    }

    @Test
    public void testCompanyAccountExpressTransferBalanceBelowZero(){
        //given
        double income = 1000;
        double outgo = 1000;
        //when
        companyAccount.incomingTransfer(income);
        double balance = companyAccount.expressOutgoingTransfer(outgo);
        //then
        assertEquals(-5, balance);
    }
}
