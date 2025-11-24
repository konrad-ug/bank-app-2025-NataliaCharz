import validator.PeselValidator;
import validator.PromoCodeValidator;

import java.util.List;

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

    @Override
    public boolean submitForLoan(double loan){
        if (checkLastThreeTransactionsAreIncome() || checkLastFiveTransactionsMustBeLargerThanLoan(loan)){
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkLastThreeTransactionsAreIncome(){
        List<Double> history = getHistory();
        for (int i = history.size() - 1; i > history.size() - 4; i--){
            if (history.get(i) < 0){
                return false;
            }
        }
        return true;
    }

    private boolean checkLastFiveTransactionsMustBeLargerThanLoan(double loan){
        List<Double> history = getHistory();
        if (history.size() < 5){
            return false;
        }
        double amount = 0;
        for (int i = history.size() - 1; i > history.size() - 6; i--){
            amount += history.get(i);
        }
        return amount > loan;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (obj.getClass() != this.getClass()) {
//            return false;
//        }
//        final PersonalAccount other = (PersonalAccount) obj;
//        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
//            return false;
//        }
//        if ((this.surname == null) ? (other.surname != null) : !this.surname.equals(other.surname)) {
//            return false;
//        }
//        if ((this.identification== null) ? (other.identification != null) : !this.identification.equals(other.identification)) {
//            return false;
//        }
//        if ((this.promoCode == null) ? (other.promoCode != null) : !this.promoCode.equals(other.promoCode)) {
//            return false;
//        }
//        return true;
//    }
}
