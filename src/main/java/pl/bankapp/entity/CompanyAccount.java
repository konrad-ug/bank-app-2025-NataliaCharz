package pl.bankapp.entity;

import pl.bankapp.validator.NipValidator;

import javax.annotation.processing.Generated;

@Generated({})
public class CompanyAccount extends Account{

    public CompanyAccount(String companyName, String nip) {
        super(companyName, nip);
        NipValidator.validateNipOrThrow(nip);
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
