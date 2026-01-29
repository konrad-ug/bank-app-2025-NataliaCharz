package pl.bankapp.validator;

import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor
public class PeselValidator {

    private static final String PESEL_REGEX = "^[0-9]{11}$";

    //Feature 3 - walidacja numeru pesel przy zakładaniu konta osobistego
    public static String validatePesel(String pesel) {
        if (pesel == null || !Pattern.matches(PESEL_REGEX, pesel)) {
            throw new IllegalArgumentException("Invalid PESEL format.");
        } else {
            return pesel;
        }
    }
}
