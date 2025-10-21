import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBalance {


    private Account test;

    @BeforeEach
    public void setUp() {
        test = new Account("Jan", "Kowalski", "87321930271", "PROMO_123");
        test.setBalance(test.getBalance() + 1000.0);
    }

    @Test
    public void addProperAmountToBalanceAsIncomingTransfer() {
        //given
        //when
        Double changedBalance = test.addIncomingTransfer(1000.0);
        //then
        assertEquals(2050.0, changedBalance);
    }

    @Test
    public void getUnchangedBalanceWhenInvalidAmountAsIncomingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            test.addIncomingTransfer(-1000);
        });
        String expectedMessage = "Wrong value or data type of incoming transfer.";
        String actualMessage = exception.getMessage();
        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void getUnchangedBalanceWhenAmountIsWrongDataTypeAsIncomingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            test.addIncomingTransfer(Double.parseDouble("income"));
        });
        String expectedMessage = "For input string: \"income\"";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void getBalanceAfterOutgoingTransfer() {
        //given
        //when
        double changedBalance = test.makeOutgoingTransfer(1000);
        //then
        assertEquals(50.0, changedBalance);
    }

    @Test
    public void getUnchangeableBalanceWhenAmountIsWrongDataTypeAsOutgoingTransfer() {
        //given + when
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            test.addIncomingTransfer(Double.parseDouble("outgoing"));
        });
        String expectedMessage = "For input string: \"outgoing\"";
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);
    }

}


