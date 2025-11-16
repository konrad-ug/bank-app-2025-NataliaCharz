import java.util.ArrayList;
import java.util.List;

public abstract class Account {

    private final String name;
    private final String identification;
    protected double balance;
    private List<Double> history = new ArrayList<>();

    Account(String name, String identification) {
        this.name = name;
        this.identification = identification;
    }

    public abstract double chargeAccount();

    public String getName() {
        return this.name;
    }

    public String getIdentification() {
        return this.identification;
    }

    public double getBalance() {
        return this.balance;
    }

    public List<Double> getHistory(){
        return this.history;
    }

    public Double incomingTransfer(double income) {
        if (income < 0) {
            throw new NumberFormatException("Wrong value of incoming transfer.");
        }
        balance = getBalance() + income;
        updateHistoryWithIncomingTransfer(income);
        return this.balance;
    }

    public double outgoingTransfer(double outgo) {
        if (getBalance() < outgo) {
            throw new NumberFormatException("Balance is lower than outgo");
        }
        if (outgo < 0) {
            throw new NumberFormatException("Wrong value of outgoing transfer.");
        }
        balance = getBalance() - outgo;
        updateHistoryWithOutgoingTransfer(outgo);
        return this.balance;
    }

    public double expressOutgoingTransfer(double outgo) {
        outgoingTransfer(outgo);
        balance = getBalance() - chargeAccount();
        updateHistoryWithExpressOutgoingTransfer();
        return this.balance;
    }

    public void updateHistoryWithIncomingTransfer(double data){
        this.history.add(data);
    }

    public void updateHistoryWithOutgoingTransfer(double data){
        double amount = -data;
        this.history.add(amount);
    }

    public void updateHistoryWithExpressOutgoingTransfer(){
        double charge = -chargeAccount();
        this.history.add(charge);
    }

}
