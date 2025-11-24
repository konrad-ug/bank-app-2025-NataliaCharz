import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestPersonalAccountLoan {

    private PersonalAccount personalAccount;

    @BeforeEach
    public void setUp(){
        String name = "John";
        String surname = "Doe";
        String pesel = "87321930271";
        personalAccount = new PersonalAccount(name, surname, pesel, null);
    }

    @Test
    public void testThreeLastTransactionsAreIncome(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        //when
        boolean lastThree = personalAccount.lastThreeTransactionsAreIncome();
        //then
        assertTrue(lastThree);
    }

    @Test
    public void testThreeLastTransactionsAreNotIncome(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.outgoingTransfer(50);
        personalAccount.incomingTransfer(1000);
        //when
        boolean lastThree = personalAccount.lastThreeTransactionsAreIncome();
        //then
        assertFalse(lastThree);
    }

    @Test
    public void testFiveLastTransactionAreBiggerThanLoan(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 4500;
        //when
        boolean test = personalAccount.lastFiveTransactionsMustBeLargerThanLoan(loan);
        //then
        assertTrue(test);
    }

    @Test
    public void testFiveLastTransactionAreSmallerThanLoan(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 5500;
        //when
        boolean test = personalAccount.lastFiveTransactionsMustBeLargerThanLoan(loan);
        //then
        assertFalse(test);
    }

    @Test
    public void testNotEnoughTransactionForFiveTransactionConditionLoan(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 500;
        //when
        boolean test = personalAccount.lastFiveTransactionsMustBeLargerThanLoan(loan);
        //then
        assertFalse(test);
    }

    @Test
    public void testSubmitForLoanWhenBothConditionsTrue(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 4000;
        //when
        boolean test = personalAccount.submitForLoan(loan);
        //then
        assertTrue(test);
        assertEquals(List.of(1000.0, 1000.0, 1000.0, 1000.0, 1000.0, 4000.0), personalAccount.getHistory());
    }

    @Test
    public void testSubmitForLoanWhenBothConditionsFalse(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.outgoingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 6000;
        //when
        boolean test = personalAccount.submitForLoan(loan);
        //then
        assertFalse(test);
        assertEquals(List.of(1000.0, 1000.0, -1000.0, 1000.0, 1000.0), personalAccount.getHistory());
    }

    @Test
    public void testSubmitForLoanWhenLastThreeTransferAreIncomeTrue(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.outgoingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 6000;
        //when
        boolean test = personalAccount.submitForLoan(loan);
        //then
        assertTrue(test);
        assertEquals(List.of(1000.0, -1000.0, 1000.0, 1000.0, 1000.0, 6000.0), personalAccount.getHistory());
    }

    @Test
    public void testSubmitForLoanWhenLastFiveTransferAreLargerThanLoanTrue(){
        //given
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        personalAccount.outgoingTransfer(1000);
        personalAccount.incomingTransfer(1000);
        double loan = 2500;
        //when
        boolean test = personalAccount.submitForLoan(loan);
        //then
        assertTrue(test);
        assertEquals(List.of(1000.0, 1000.0, 1000.0, -1000.0, 1000.0, 2500.0), personalAccount.getHistory());
    }
}
