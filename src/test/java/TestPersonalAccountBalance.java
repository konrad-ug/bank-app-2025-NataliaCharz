import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPersonalAccountBalance {

    private PersonalAccount personalAccount;
    private CompanyAccount companyAccount;

    @BeforeEach
    public void setUp(){
        String name = "John";
        String surname = "Doe";
        String pesel = "87321930271";
        personalAccount = new PersonalAccount(name, surname, pesel, null);
    }

    @Test
    public void testPersonalAccountAddProperAmountToBalanceIncomingTransfer() {
        //given
        double transfer = 1000.0;
        //when
        Double balance = personalAccount.incomingTransfer(transfer);
        //then
        assertEquals(1000.0, balance);
    }

    @Test
    public void testPersonalAccountThrowExceptionWhenInvalidAmountIncomingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            personalAccount.incomingTransfer(-1000);
        });
        String expectedMessage = "Wrong value of incoming transfer.";
        String actualMessage = exception.getMessage();
        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testPersonalAccountGetBalanceAfterOutgoingTransfer() {
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
    public void testPersonalAccountThrowExceptionWhenOutgoingTransferValueHigherThanBalance(){
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
    public void testPersonalAccountExpressOutgoingTransfer(){
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
    public void testPersonalAccountExpressTransferBalanceBelowZero(){
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


