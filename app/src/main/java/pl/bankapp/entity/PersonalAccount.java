package pl.bankapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.bankapp.validator.PeselValidator;
import pl.bankapp.validator.PromoCodeValidator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class PersonalAccount extends Account {

    @Column(name="surname")
    private String surname;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
    private List<HistoryTransfer> historyTransfers = new ArrayList<>();
    private String promoCode;

    public PersonalAccount(String name, String surname, String pesel) {
        super(name, PeselValidator.validatePesel(pesel));
        this.surname = surname;
    }

    public void usePromoCode(String promoCode) {
        String pesel = getIdentification();
        if (promoCode != null && PromoCodeValidator.validatePromoCode(promoCode) && PromoCodeValidator.validatePromoCodeWithCorrectYearBorn(pesel)) {
            super.balance = super.balance + 50.0;
        }
    }

    @Override
    public double chargeAccount() {
        return 1;
    }

    @Override
    public boolean submitForLoan(double loan) {
        if (checkLastThreeTransactionsAreIncome() || checkLastFiveTransactionsMustBeLargerThanLoan(loan)) {
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkLastThreeTransactionsAreIncome() {
        List<Double> history = getHistory();
        for (int i = history.size() - 1; i > history.size() - 4; i--) {
            if (history.get(i) < 0) {
                return false;
            }
        }
        return true;
    }

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
