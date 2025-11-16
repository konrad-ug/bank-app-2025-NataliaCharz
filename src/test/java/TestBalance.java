import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestBalance {

    private PersonalAccount personalAccount;
    private CompanyAccount companyAccount;

    @BeforeEach
    public void setUp(){
        String name = "John";
        String surname = "Doe";
        String pesel = "87321930271";
        personalAccount = new PersonalAccount(name, surname, pesel, null);
        String companyName = "Alfa";
        String nip = "12345678901";
        companyAccount = new CompanyAccount(companyName, nip);
    }

    @Test
    public void addProperAmountToBalanceAsIncomingTransfer() {
        //given
        double transfer = 1000.0;
        //when
        Double balance = personalAccount.incomingTransfer(transfer);
        //then
        assertEquals(1000.0, balance);
    }

    @Test
    public void throwExceptionWhenInvalidAmountAsIncomingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            personalAccount.incomingTransfer(-1000);
        });
        String expectedMessage = "Wrong value or data type of incoming transfer.";
        String actualMessage = exception.getMessage();
        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetBalanceAfterOutgoingTransfer() {
        //given
        double income = 2000;
        double outgo = 1000;
        //when
        personalAccount.incomingTransfer(income);
        double balance = personalAccount.outgoingTransfer(outgo);
        //then
        assertEquals(1000.0, balance);
    }

    @Test
    public void testThrowExceptionWhenOutgoingTransferValueHigherThanBalance(){
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            personalAccount.outgoingTransfer(1000);
        });
        String expectedMessage = "Balance is lower than outgo";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testExpressOutgoingTransferForPersonalAccount(){
        //given
        double income = 2000;
        double outgo = 1000;
        //when
        personalAccount.incomingTransfer(income);
        double balance = personalAccount.expressOutgoingTransfer(outgo);
        //then
        assertEquals(999, balance);
    }

    @Test
    public void testExpressOutgoingTransferForCompanyAccount(){
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
    public void testBalanceLessThanZeroWithExpressTransferForPersonalAccount(){
        //given
        double income = 1000;
        double outgo = 1000;
        //when
        personalAccount.incomingTransfer(income);
        double balance = personalAccount.expressOutgoingTransfer(outgo);
        //then
        assertEquals(-1, balance);

    }


}


