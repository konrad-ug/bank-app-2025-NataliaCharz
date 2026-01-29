package pl.bankapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.bankapp.exception.IncomingTransactionFailedException;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.service.SMTPClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@ToString
@Access(AccessType.FIELD)
public abstract class Account {

    /**
     * Feature 1 – Zakładanie konta osobistego
     * Feature 2 - Dodanie numeru pesel(identification dla PersonalAccount lub NIP dla CompanyAccount). Pesel jako String, bo może zaczynać się od 0
     */
    @Id
    @Column
    protected String identification;
    @Column(name = "name")
    protected String name;
    @Column(name = "balance")
    protected double balance = 0.0;
    /**
     * Feature 11 - historia transakcji na koncie
     */
    @Transient
    private List<Double> history = new ArrayList<>();

    protected Account(String name, String identification) {
        this.name = name;
        this.identification = identification;
    }


    /**
     * Feature 6 - realizacja przelewów przychodzących
     */
    public Double incomingTransfer(double income) {
        if (income < 0) {
            throw new IncomingTransactionFailedException("Wrong value of incoming transfer.");
        }
        balance = getBalance() + income;
        updateHistoryWithIncomingTransfer(income);
        return this.balance;
    }

    /**
     * Feature 6 - realizacja przelewów wychodzących
     */
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

    /**
     * Feature 8 - realizacja ekspresowych przelewów wychodzących
     */
    public double expressOutgoingTransfer(double outgo) {
        outgoingTransfer(outgo);
        balance = getBalance() - chargeAccount();
        updateHistoryWithExpressOutgoingTransfer();
        return this.balance;
    }

    /**
     * Feature 8 - opłata za przelew ekspresowy
     */
    public abstract double chargeAccount();

    /**
     * Feature 11 - historia transakcji na koncie. Wywołanie w metodzie incomingTransfer
     */
    public void updateHistoryWithIncomingTransfer(double data) {
        this.history.add(data);
    }

    /**
     * Feature 11 - historia transakcji na koncie. Wywołanie w metodzie outgoingTransfer
     */
    public void updateHistoryWithOutgoingTransfer(double data) {
        double amount = -data;
        this.history.add(amount);
    }

    /**
     * Feature 11 - historia transakcji na koncie. Wywołanie w metodzie expressOutgoingTransfer
     */
    public void updateHistoryWithExpressOutgoingTransfer() {
        double charge = -chargeAccount();
        this.history.add(charge);
    }

    /**
     * Feature 12 - zaciąganie kredytu
     */
    public abstract boolean submitForLoan(double loan);

    /**
     * Feature 19 - wysyłanie historii przelewów na email
     */
    public boolean sendHistoryViaEmail(String email, SMTPClient client) {
        String subject = "Account Transfer History " + LocalDate.now();
        StringBuilder text = new StringBuilder();
        if (this instanceof PersonalAccount) {
            text.append("Personal account history: ").append(getHistory());
        } else {
            text.append("Company account history: ").append(getHistory());
        }
        return client.send(subject, text.toString(), email);
    }

}
