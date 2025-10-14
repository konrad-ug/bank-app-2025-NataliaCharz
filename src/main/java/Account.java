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
}