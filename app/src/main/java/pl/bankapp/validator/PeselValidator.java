package pl.bankapp.validator;

import java.util.regex.Pattern;

public class PeselValidator {

    private static final String PESEL_REGEX = "^[0-9]{11}$";

    private PeselValidator(){
    }

    public static String validatePesel(String pesel) {
        if (pesel == null || !Pattern.matches(PESEL_REGEX, pesel)) {
            throw new IllegalArgumentException("Invalid PESEL format.");
        } else {
            return pesel;
        }
    }
}
