import java.util.regex.Pattern;

public class Account{

    private String name;
    private String surname;
    private String pesel;
    private double balance;
    private String promoCode;

    public Account(String name, String surname){
        this.name = name;
        this.surname = surname;
    }

    public Account(String name, String surname, String pesel){
        this.name = name;
        this.surname = surname;
        this.pesel = PeselValidator.validatePesel(pesel);
    }

    public Account(String name, String surname, String pesel, String promoCode){
        this.name = name;
        this.surname = surname;
        this.pesel = PeselValidator.validatePesel(pesel);
        if (promoCode != null){
            if(PromoCodeValidator.validatePromoCode(promoCode) && PromoCodeValidator.validatePromoCodeWithCorrectYearBorn(this.pesel)){
                this.balance = getBalance() + 50.0;
            };
        }
    }


    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getSurname(){
        return this.surname;
    }

    public void setPesel(String pesel){
        this.pesel = pesel;
    }

    public String getPesel(){
        return this.pesel;
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public Double addIncomingTransfer(double income) {
        String incomeToString = Double.toString(income);
        if (income < 0 && !Pattern.matches("^[0-9]*/.[0-9]*$", incomeToString)){
            throw new NumberFormatException("Wrong value or data type of incoming transfer.");
        }
        this.setBalance(getBalance() + income);
        return this.balance;
    }

    public double makeOutgoingTransfer(double outgo) {
        String outgoToString = Double.toString(outgo);
        if (outgo < 0 && !Pattern.matches("^[0-9]*/.[0-9]*$", outgoToString)){
            throw new NumberFormatException("Wrong value or data type of outgoing transfer.");
        }
        this.setBalance(getBalance() - outgo);
        return this.balance;
    }
}
