import java.util.regex.Pattern;

public class PersonalAccount extends Account {

    private String surname;
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

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
        if (PromoCodeValidator.validatePromoCode(promoCode) && PromoCodeValidator.validatePromoCodeWithCorrectYearBorn(getIdentification())) {
            balance += 50.0;
        }
    }

    @Override
    public double chargeAccount() {
        return 1;
    }
}
