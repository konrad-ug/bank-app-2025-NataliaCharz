package pl.bankapp.entity;

import lombok.Getter;
import lombok.Setter;
import pl.bankapp.exception.IncomingTransactionFailedException;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.service.SMTPClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Account {

    protected String name;
    protected String identification;
    protected double balance;
    private List<Double> history = new ArrayList<>();

    public Account(String name, String identification) {
        this.name = name;
        this.identification = identification;
    }

    public abstract double chargeAccount();

    public abstract boolean submitForLoan(double loan);

    public Double incomingTransfer(double income) {
        if (income < 0) {
            throw new IncomingTransactionFailedException("Wrong value of incoming transfer.");
        }
        balance = getBalance() + income;
        updateHistoryWithIncomingTransfer(income);
        return this.balance;
    }

    public double outgoingTransfer(double outgo) {
        if (getBalance() < outgo) {
            throw new OutgoingTransactionFailedException("Balance is lower than outgo");
        }
        if (outgo <= 0) {
            throw new OutgoingTransactionFailedException("Wrong value of outgoing transfer.");
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

    public void updateHistoryWithIncomingTransfer(double data) {
        this.history.add(data);
    }

    public void updateHistoryWithOutgoingTransfer(double data) {
        double amount = -data;
        this.history.add(amount);
    }

    public void updateHistoryWithExpressOutgoingTransfer() {
        double charge = -chargeAccount();
        this.history.add(charge);
    }

    public boolean sendHistoryViaEmail(String email, SMTPClient client) {
        String subject = "Account Transfer History " + LocalDate.now();
        StringBuilder text = new StringBuilder();
        if (this instanceof PersonalAccount) {
            text.append("Personal account history: ").append(getHistory());
        } else if (this instanceof CompanyAccount) {
            text.append("Company account history: ").append(getHistory());
        } else {
            text.append("Account history: ").append(getHistory());
        }
        return client.send(subject, text.toString(), email);
    }

}
