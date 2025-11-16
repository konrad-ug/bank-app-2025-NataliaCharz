import validator.PeselValidator;
import validator.PromoCodeValidator;

public class PersonalAccount extends Account {

    private final String surname;
    private String promoCode;

    public PersonalAccount(String name, String surname, String pesel, String promoCode) {
        super(name, PeselValidator.validatePesel(pesel));
        this.surname = surname;
        if (promoCode != null && PromoCodeValidator.validatePromoCode(promoCode) && PromoCodeValidator.validatePromoCodeWithCorrectYearBorn(pesel)) {
            super.balance = super.balance + 50.0;
        }
    }

    public String getSurname() {
        return this.surname;
    }

    @Override
    public double chargeAccount() {
        return 1;
    }
}
