package validator;

import java.util.regex.Pattern;

public class PromoCodeValidator {

    private static final String ACTUAL_PROMO_CODE = "^PROMO_.{3}$";

    private PromoCodeValidator(){
    }

    public static boolean validatePromoCode(String promoCode){
        if (!Pattern.matches(ACTUAL_PROMO_CODE, promoCode)){
            return false;
        } else {
            return true;
        }
    }

    public static boolean validatePromoCodeWithCorrectYearBorn(String pesel) {
        int year = Integer.parseInt(pesel.substring(0,2));
        return year >= 60;
    }

}
