package validator;

import java.util.regex.Pattern;

public class NipValidator {

    private static final String NIP_REGEX = "^[0-9]{10}$";

    private NipValidator(){
    }

    public static String validateNip(String nip) {
        if (nip == null || !Pattern.matches(NIP_REGEX, nip)) {
            return "Invalid";
        } else {
            return nip;
        }
    }
}
