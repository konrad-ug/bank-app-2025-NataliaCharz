package validator;

import java.util.regex.Pattern;

public class PeselValidator {

    private static final String PESEL_REGEX = "^[0-9]{11}$";

    private PeselValidator(){
    }

    public static String validatePesel(String pesel) {
        if (pesel == null || !Pattern.matches(PESEL_REGEX, pesel)) {
            return "Invalid";
        } else {
            return pesel;
        }
    }
}
