import validator.PeselValidator;
import validator.PromoCodeValidator;

import java.util.List;
import java.util.NoSuchElementException;

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

    public boolean submitForLoan(double loan){
        if (lastThreeTransactionsAreIncome() || lastFiveTransactionsMustBeLargerThanLoan(loan)){
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    public boolean lastThreeTransactionsAreIncome(){
        List<Double> history = getHistory();
        for (int i = history.size() - 1; i > history.size() - 4; i--){
            if (history.get(i) < 0){
                return false;
            }
        }
        return true;
    }

    public boolean lastFiveTransactionsMustBeLargerThanLoan(double loan){
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
}
