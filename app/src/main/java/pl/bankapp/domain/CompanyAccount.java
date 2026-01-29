package pl.bankapp.domain;

import pl.bankapp.entity.Account;

import javax.annotation.processing.Generated;

@Generated({})
public class CompanyAccount extends Account {

    /**
     * Feature 7 – Zakładanie konta firmowego (CompanyAccount dziedziczy po klasie Account)
     */
    CompanyAccount(String companyName, String nip) {
        super(companyName, nip);
        this.identification = nip;
    }

    /**
     * Feature 8 - opłata za przelew ekspresowy
     */
    @Override
    public double chargeAccount() {
        return 5;
    }

    /**
     * Feature 13 - zaciąganie kredytu dla konta firmowego
     */
    @Override
    public boolean submitForLoan(double loan) {
        if (checkBalanceForLoan(loan) && checkZusTransfer()) {
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Feature 13 - saldo konta musi być większe niż dwukrotność wnioskowanej kwoty kredytu
     */
    public boolean checkBalanceForLoan(double loan) {
        return this.balance > loan * 2;
    }

    /**
     * Feature 13 - na koncie musi znajdować się przelew z ZUS o wartości 1775 zł
     */
    public boolean checkZusTransfer() {
        return getHistory().contains(-1775.0);
    }

}
