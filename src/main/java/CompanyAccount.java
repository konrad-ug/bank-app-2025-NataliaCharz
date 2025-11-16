import validator.NipValidator;

public class CompanyAccount extends Account{

    CompanyAccount(String companyName, String nip){
        super(companyName, NipValidator.validateNip(nip));
    }

    @Override
    public double chargeAccount() {
        return 5;
    }

}
