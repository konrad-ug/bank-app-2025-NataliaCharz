import java.util.regex.Pattern;

public abstract class Account {

    private final String name;
    private final String identification;
    protected double balance;

    Account(String name, String identification){
        this.name = name;
        this.identification = identification;
    }

    public abstract double chargeAccount();


    public String getName(){
        return this.name;
    }

    public String getIdentification() {
        return this.identification;
    }

    public double getBalance(){
        return this.balance;
    }

    public Double incomingTransfer(double income) {
        if (income < 0){
            throw new NumberFormatException("Wrong value of incoming transfer.");
        }
        balance = getBalance() + income;
        return this.balance;
    }

    public double outgoingTransfer(double outgo) {
        if (getBalance() < outgo){
            throw new NumberFormatException("Balance is lower than outgo");
        }
        if (outgo < 0){
            throw new NumberFormatException("Wrong value of outgoing transfer.");
        }
        balance = getBalance() - outgo;
        return this.balance;
    }

    public double expressOutgoingTransfer(double outgo) {
        if (getBalance() < outgo ){
            throw new NumberFormatException("Wrong value of outgoing transfer");
        }
        if (outgo < 0){
            throw new NumberFormatException("Wrong value of outgoing transfer.");
        }
        balance = getBalance() - outgo - chargeAccount();
        return this.balance;
    }

}
