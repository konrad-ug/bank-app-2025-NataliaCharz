import java.util.regex.Pattern;

public class PeselValidator {

    private static final String PESEL_REGEX = "^[0-9]{11}$";

    private PeselValidator(){
    }

    public static String validatePesel(String pesel) {
        if (!Pattern.matches(PESEL_REGEX, pesel) || pesel == null) {
            return "Invalid";
        } else {
            return pesel;
        }
    }
}
