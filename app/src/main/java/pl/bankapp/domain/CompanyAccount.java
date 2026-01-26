package pl.bankapp.domain;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import pl.bankapp.entity.Account;
import pl.bankapp.service.NipValidator;

import javax.annotation.processing.Generated;

@Generated({})
public class CompanyAccount extends Account {

    CompanyAccount(String companyName, String nip) {
        super(companyName, nip);
        this.identification = nip;
    }

    @Override
    public double chargeAccount() {
        return 5;
    }

    @Override
    public boolean submitForLoan(double loan) {
        if (checkBalanceForLoan(loan) && checkZusTransfer()){
            this.incomingTransfer(loan);
            return true;
        } else {
            return false;
        }
    }

    public boolean checkBalanceForLoan(double loan){
        return this.balance > loan * 2;
    }

    public boolean checkZusTransfer(){
        return getHistory().contains(-1775.0);
    }

}
