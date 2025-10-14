import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class TestAccount {

    @Test
    public void testAccountCreation() {
        //given
        String name = "John";
        String surname = "Doe";
        //when
        Account account = new Account(name, surname);
        //then
        assertEquals("John", account.getName());
        assertEquals("Doe", account.getSurname());
        assertEquals(0.0, account.getBalance());
    }

    @Test
    public void testAccountWithGivenBalance(){
        //given
        String name = "John";
        String surname = "Doe";
        double balance = 10000.0;
        //when
        Account account = new Account(name, surname);
        account.setBalance(balance);
        //then
        assertEquals(10000.0, account.getBalance());
    }



}