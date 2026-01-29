package pl.bankapp.validator;

import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor
public class PromoCodeValidator {

    private static final String ACTUAL_PROMO_CODE = "^PROMO_.{3}$";

    //Feature 4 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego. Kod promocyjny ma format "PROMO_XYZ", gdzie XYZ to dowolne trzy znaki.
    public static boolean validatePromoCode(String promoCode){
        if (!Pattern.matches(ACTUAL_PROMO_CODE, promoCode)){
            return false;
        } else {
            return true;
        }
    }

    //Feature 5 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego. Kod promocyjny jest ważny tylko dla osób urodzonych po 1960 roku.
    public static boolean validatePromoCodeWithCorrectYearBorn(String pesel) {
        int year = Integer.parseInt(pesel.substring(0,2));
        return year >= 60;
    }

}
