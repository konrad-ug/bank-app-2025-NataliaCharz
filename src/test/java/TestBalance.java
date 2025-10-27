import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBalance {

    private PersonalAccount personalAccount;

    @BeforeEach
    public void setUp(){
        String name = "John";
        String surname = "Doe";
        String pesel = "87321930271";
        personalAccount = new PersonalAccount(name, surname, pesel, null);
    }

    @Test
    public void addProperAmountToBalanceAsIncomingTransfer() {
        //given
        double transfer = 1000.0;
        //when
        Double changedBalance = personalAccount.incomingTransfer(transfer);
        //then
        assertEquals(1000.0, changedBalance);
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
        //when
        personalAccount.incomingTransfer(income);
        double changedBalance = personalAccount.outgoingTransfer(1000);
        //then
        assertEquals(1000.0, changedBalance);
    }

    @Test
    public void testThrowExceptionWhenOutgoingTransferValueHigherThanBalance(){
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            personalAccount.outgoingTransfer(1000);
        });
        String expectedMessage = "Wrong value of outgoing transfer";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);
    }

}


