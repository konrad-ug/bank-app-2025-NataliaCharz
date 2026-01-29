package pl.bankapp.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.bankapp.dto.PersonalAccountPartialUpdateDTO;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.entity.TransferType;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class AccountRegistryTest {

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

    @AfterEach
    public void tearDown() {
        registry = new AccountsRegistry();
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
    public void shouldUpdatePersonalAccount() {
        //given
        String pesel = "22222222222";
        PersonalAccount updateAccount = new PersonalAccount("Adam", "Nowak", pesel);
        //when
        PersonalAccount updated = registry.updatePersonalAccount(pesel, updateAccount);
        //then
        assertEquals("Adam", updated.getName());
        assertEquals("Nowak", updated.getSurname());
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistingAccount() {
        //given
        String pesel = "99999999999";
        PersonalAccount updateAccount = new PersonalAccount("Adam", "Nowak", pesel);
        //when
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            registry.updatePersonalAccount(pesel, updateAccount);
        });
        //then
        String expectedMessage = "No Account with provided pesel: " + pesel;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldDeletePersonalAccount() {
        //given
        String pesel = "33333333333";
        //when
        boolean isDeleted = registry.deleteAccount(pesel);
        //then
        assertTrue(isDeleted);
        assertThrows(NoSuchElementException.class, () -> {
            registry.findAccountByPesel(pesel);
        });
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistingAccount() {
        //given
        String pesel = "88888888888";
        //when
        boolean isDeleted = registry.deleteAccount(pesel);
        //then
        assertFalse(isDeleted);
    }

    @ParameterizedTest
    @MethodSource
    public void shouldPartialUpdateBothNameAndOrSurname(String pesel, PersonalAccountPartialUpdateDTO updateDTO, String expectedName, String expectedSurname) {
        //when
        PersonalAccount updated = registry.partialUpdatePersonalAccount(pesel, updateDTO);
        //then
        assertEquals(expectedName, updated.getName());
        assertEquals(expectedSurname, updated.getSurname());
    }

    static Stream <Arguments> shouldPartialUpdateBothNameAndOrSurname() {
        return Stream.of(
                Arguments.of("11111111111", new PersonalAccountPartialUpdateDTO("Mike", null), "Mike", "Maria"),
                Arguments.of("11111111111", new PersonalAccountPartialUpdateDTO(null, "Smith"), "Jose", "Smith"),
                Arguments.of("11111111111", new PersonalAccountPartialUpdateDTO("Jan", "Kowalski"), "Jan", "Kowalski")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void shouldCreateTransferWhenAccountExists(TransferDTO transferDTO, String pesel, double expectedBalance) {
        //given
        if (transferDTO.getType() == TransferType.OUTGOING || transferDTO.getType() == TransferType.EXPRESS) {
            registry.findAccountByPesel(pesel).incomingTransfer(150.0);
        }
        //when
        registry.createTransfer(pesel, transferDTO);
        double actualBalance = registry.findAccountByPesel(pesel).getBalance();
        //then
        assertEquals(expectedBalance, actualBalance);
    }

    static Stream <Arguments> shouldCreateTransferWhenAccountExists() {
        return Stream.of(
                Arguments.of(new TransferDTO(200.0, TransferType.INCOMING), "12345678905", 200.0),
                Arguments.of(new TransferDTO(50.0, TransferType.OUTGOING), "12345678905", 100.0),
                Arguments.of(new TransferDTO(50.0, TransferType.EXPRESS), "12345678905", 99.0)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void shouldThrowExceptionWhenCreatingTransfer(TransferDTO transferDTO, String pesel, String expectedMessage) {
        //given
        Exception exception = assertThrows(Exception.class, () -> {
            registry.createTransfer(pesel, transferDTO);
        });
        //when
        String actualMessage = exception.getMessage();
        //then
        assertEquals(expectedMessage, actualMessage);

    }

    static Stream <Arguments> shouldThrowExceptionWhenCreatingTransfer() {
        return Stream.of(
                Arguments.of(new TransferDTO(100.0, null), "12345678905", "Wrong type of transfer"),
                Arguments.of(new TransferDTO(-50.0, TransferType.INCOMING), "12345678905",  "Wrong value of incoming transfer."),
                Arguments.of(null, "12345678905", "Provide transfer request"),
                Arguments.of(new TransferDTO(100.0, TransferType.EXPRESS), "99999999999", "No Account with provided pesel: 99999999999")
        );
    }
}
