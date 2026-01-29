package pl.bankapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.bankapp.validator.PeselValidator;
import pl.bankapp.validator.PromoCodeValidator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PersonalAccount extends Account {

    //Feature 1 – Zakładanie konta osobistego (PersonalAccount dziedziczy po klasie Account)
    @Column(name="surname")
    private String surname;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
    private List<HistoryTransaction> historyTransactions = new ArrayList<>();
    private String promoCode;

    //Feature 3 - walidacja numeru pesel przy zakładaniu konta osobistego (validator/PeselValidator.java)
    public PersonalAccount(String name, String surname, String pesel) {
        super(name, PeselValidator.validatePesel(pesel));
        this.surname = surname;
    }

    //Feature 4 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego (validator/PromoCodeValidator.java)
    //Feature 5 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego. Kod promocyjny jest ważny tylko dla osób urodzonych po 1960 roku.
    public void usePromoCode(String promoCode) {
        String pesel = getIdentification();
        if (promoCode != null && PromoCodeValidator.validatePromoCode(promoCode) && PromoCodeValidator.validatePromoCodeWithCorrectYearBorn(pesel)) {
            super.balance = super.balance + 50.0;
        }
    }

    //Feature 8 - opłata za przelew ekspresowy
    @Override
    public double chargeAccount() {
        return 1;
    }

    //Feature 12 - zaciąganie kredytu dla konta osobistego
    @Override
    public boolean submitForLoan(double loan) {
        if (checkLastThreeTransactionsAreIncome() || checkLastFiveTransactionsMustBeLargerThanLoan(loan)) {
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    //Feature 12 - Ostatnie trzy zaksięgowane transakcje powinny być transakcjami wpłaty
    private boolean checkLastThreeTransactionsAreIncome() {
        List<Double> history = getHistory();
        for (int i = history.size() - 1; i > history.size() - 4; i--) {
            if (history.get(i) < 0) {
                return false;
            }
        }
        return true;
    }

    //Feature 12 - Suma ostatnich pięciu transakcji (konto musi mieć co najmniej pięć transakcji) powinna być większa niż kwota wnioskowanego kredytu.
    private boolean checkLastFiveTransactionsMustBeLargerThanLoan(double loan) {
        List<Double> history = getHistory();
        if (history.size() < 5) {
            return false;
        }
        double amount = 0;
        for (int i = history.size() - 1; i > history.size() - 6; i--) {
            amount += history.get(i);
        }
        return amount > loan;
    }
}
