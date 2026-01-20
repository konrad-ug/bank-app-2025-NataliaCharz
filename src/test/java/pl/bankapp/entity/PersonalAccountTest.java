package pl.bankapp.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PersonalAccountTest {

    @Test
    public void shouldCreateAccount() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "73345678901";
        //when
        PersonalAccount personalAccount = new PersonalAccount(name, surname, pesel);
        //then
        assertEquals(name, personalAccount.getName());
        assertEquals(surname, personalAccount.getSurname());
        assertEquals(0, personalAccount.getBalance());
    }

    @Test
    public void testAccountWithGivenBalance() {
        //given + when
        String name = "John";
        String surname = "Doe";
        String pesel = "73345678901";
        PersonalAccount personalAccount = new PersonalAccount(name, surname, pesel);
        double balance = 0;
        //then
        assertEquals(0, personalAccount.getBalance());
    }

    @ParameterizedTest
    @MethodSource
    public void shouldReturnInvalidDuringPeselValidation(String pesel) {
        //given
        String name = "John";
        String surname = "Doe";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel);
        //then
        assertEquals("Invalid", account.getIdentification());
    }

    static Stream<Arguments> shouldReturnInvalidDuringPeselValidation() {
        return Stream.of(
                Arguments.of("123"),
                Arguments.of("JohnDoe"),
                Arguments.of("1234567890A"),
                Arguments.of("12345678903243543454")
        );
    }


    @Test
    public void testPeselInvalidWhenNull() {
        //given
        String name = "John";
        String surname = "Doe";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, null);
        //then
        assertEquals("Invalid", account.getIdentification());
    }

    @ParameterizedTest
    @MethodSource
    public void testPromoCodeWhenValidShouldAddAmountToBalance(String promoCode, double expectedBalance) {
        //given
        PersonalAccount account = new PersonalAccount("John", "Doe", "73345678901");
        // when
        account.usePromoCode(promoCode);
        //then
        assertEquals(expectedBalance, account.getBalance());
    }
    static Stream<Arguments> testPromoCodeWhenValidShouldAddAmountToBalance() {
        return Stream.of(
                Arguments.of("PROMO_123", 50.0),
                Arguments.of("incorrect", 0.0),
                Arguments.of("PROMO_1233", 0.0),
                Arguments.of(null, 0.0)
        );
    }

    @Test
    public void testPromoCodeValidPersonTooOldBalanceNotChanged() {
        //given
        String name = "John";
        String surname = "Doe";
        String pesel = "19345678901";
        String promoCode = "PROMO_123";
        //when
        PersonalAccount account = new PersonalAccount(name, surname, pesel);
        account.usePromoCode(promoCode);
        //then
        assertEquals(0.0, account.getBalance());
    }

}