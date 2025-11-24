import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccountRegistry {

    private AccountsRegistry registry = new AccountsRegistry();

    @BeforeEach
    public void setUp() {
        PersonalAccount a1 = new PersonalAccount("John", "Doe", "12345678905", null);
        PersonalAccount a2 = new PersonalAccount("Julia", "Smith", "65473890981", null);
        PersonalAccount a3 = new PersonalAccount("Jose", "Maria", "11111111111", null);
        PersonalAccount a4 = new PersonalAccount("Witold", "Pat", "22222222222", null);
        PersonalAccount a5 = new PersonalAccount("Uncle", "Ben", "33333333333", null);
        registry.addAccount(a1);
        registry.addAccount(a2);
        registry.addAccount(a3);
        registry.addAccount(a4);
        registry.addAccount(a5);
    }

    @Test
    public void shouldAddAccountToRegistry() {
        //given
        PersonalAccount account = new PersonalAccount("Mia", "Kunis", "12345612345", null);
        //when
        registry.addAccount(account);
        //then
        assertTrue(registry.getAllAccounts().contains(account));
    }

    @Test
    public void shouldFindAccountByIdInRegistry() {
        //given
        String pesel = "11111111111";
        String name = "Jose";
        String surname = "Maria";
        String p = "11111111111";
        //when
        PersonalAccount account = registry.findAccountByPesel(pesel);
        //then
        assertEquals(name, account.getName());
        assertEquals(surname, account.getSurname());
        assertEquals(p, account.getIdentification());
    }

    @Test
    public void shouldThrowExceptionWhenNoAccountFoundWithGivenPesel() {
        //given
        String pesel = "12345609876";
        //when
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            registry.findAccountByPesel(pesel);
        });
        String expectedMessage = "No Account with provided pesel " + pesel;
        String actualMessage = exception.getMessage();
        //then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldReturnNumberOfAccountsInRegistry(){
        //given
        int number = 5;
        //when
        int amonut = registry.getAmountOfAccounts();
        //then
        assertTrue(number == amonut);
    }

    @Test
    public void shouldReturnEveryAccountInRegistry(){
        //given
        int number = 5;
        //when
        List<PersonalAccount> accounts = registry.getAllAccounts();
        //then
        assertEquals(number, accounts.size());
    }
}
