package pl.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.bankapp.entity.PersonalAccount;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccountRegistry {

    private AccountsRegistry registry = new AccountsRegistry();

    @BeforeEach
    public void setUp() {
        PersonalAccount a1 = new PersonalAccount("John", "Doe", "12345678905");
        PersonalAccount a2 = new PersonalAccount("Julia", "Smith", "65473890981");
        PersonalAccount a3 = new PersonalAccount("Jose", "Maria", "11111111111");
        PersonalAccount a4 = new PersonalAccount("Witold", "Pat", "22222222222");
        PersonalAccount a5 = new PersonalAccount("Uncle", "Ben", "33333333333");
        registry.addAccount(a1);
        registry.addAccount(a2);
        registry.addAccount(a3);
        registry.addAccount(a4);
        registry.addAccount(a5);
    }

    @Test
    public void shouldAddAccountToRegistry() {
        //given
        PersonalAccount account = new PersonalAccount("Mia", "Kunis", "12345612345");
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
    public void shouldThrowExceptionWhenNoAccountWithGivenPesel() {
        // given
        String pesel = "12345609876";
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
           registry.findAccountByPesel(pesel);
        });
        // when
        String expected = "No Account with provided pesel: " + pesel;
        String actual = exception.getMessage();
        // then
        assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnNumberOfAccountsInRegistry() {
        //given
        int number = 5;
        //when
        int amonut = registry.getAmountOfAccounts();
        //then
        assertTrue(number == amonut);
    }

    @Test
    public void shouldReturnEveryAccountInRegistry() {
        //given
        int number = 5;
        //when
        List<PersonalAccount> accounts = registry.getAllAccounts();
        //then
        assertEquals(number, accounts.size());
    }

    @Test
    public void shouldUpdateOnlyName() {
        //given
        PersonalAccount account = new PersonalAccount("John", "Doe", "12345678901");
        registry.addAccount(account);

        //when
        PersonalAccount updated = registry.partialUpdatePersonalAccount("12345678901", "Mike", null);

        //then
        assertEquals("Mike", updated.getName());
        assertEquals("Doe", updated.getSurname());
    }

    @Test
    public void shouldUpdateOnlySurname() {
        //given
        PersonalAccount account = new PersonalAccount("John", "Doe", "12345678901");
        registry.addAccount(account);

        //when
        PersonalAccount updated = registry.partialUpdatePersonalAccount("12345678901", null, "Smith");

        //then
        assertEquals("John", updated.getName());
        assertEquals("Smith", updated.getSurname());
    }

    @Test
    public void shouldUpdateBothNameAndSurname() {
        //given
        PersonalAccount account = new PersonalAccount("John", "Doe", "12345678901");
        registry.addAccount(account);

        //when
        PersonalAccount updated = registry.partialUpdatePersonalAccount("12345678901", "Alice", "Brown");

        //then
        assertEquals("Alice", updated.getName());
        assertEquals("Brown", updated.getSurname());
    }

    @Test
    public void shouldRemainUnUpdatedWhenNoArgumentsProvided() {
        //given
        PersonalAccount account = new PersonalAccount("John", "Doe", "12345678901");
        registry.addAccount(account);

        //when
        PersonalAccount updated = registry.partialUpdatePersonalAccount("12345678901");

        //then
        assertEquals("John", updated.getName());
        assertEquals("Doe", updated.getSurname());
    }
}
