import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestPersonalAccount {

    private PersonalAccount personalAccount;

    @BeforeEach
    public void setUp() {
        String name = "John";
        String surname = "Doe";
        personalAccount = new PersonalAccount(
                name,
                surname,
                "12345678901",
                null);
    }

    @Test
    public void testAccountCreation() {
        //given
        String name = "John";
        String surname = "Doe";
        //when
        //then
        assertEquals(name, personalAccount.getName());
        assertEquals(surname, personalAccount.getSurname());
        assertEquals(0, personalAccount.getBalance());
    }

    @Test
    public void testAccountWithGivenBalance() {
        //given
        double balance = 0;
        //when
        //then
        assertEquals(0, personalAccount.getBalance());
    }

    @Test
    public void testPeselShouldSetToInvalidWhenTooShort() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "123";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, null);
        //then
        assertEquals("Invalid", account.getIdentification());
    }

    @Test
    public void testPeselShouldSetToInvalidWhenContainsLetters() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "John";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, null);
        //then
        assertEquals("Invalid", account.getIdentification());
    }

    @Test
    public void testPeselInvalidWhenNull() {
        //given
        String name = "John";
        String surname = "Doe";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, null, null);
        //then
        assertEquals("Invalid", account.getIdentification());
    }

    @Test
    public void testPromoCodeWhenValidShouldAddAmountToBalance() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "73345678901";
        String promoCode = "PROMO_123";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, promoCode);
        //then
        assertEquals(50.0, account.getBalance());
    }

    @Test
    public void testPromoCodeInvalidBalanceNotChanged() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "73345678901";
        String promoCode = "incorrect";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, promoCode);
        //then
        assertEquals(0.0, account.getBalance());
    }

    @Test
    public void testPromoCodeValidPersonTooOldBalanceNotChanged(){
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "19345678901";
        String promoCode = "PROMO_123";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, promoCode);
        //then
        assertEquals(0.0, account.getBalance());
    }

    @Test
    public void testPromoCodeWhenInValidShouldAddZeroToBalance() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "73345678901";
        String promoCode = "PROMO_1233";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel, promoCode);
        //then
        assertEquals(0, account.getBalance());
    }
}