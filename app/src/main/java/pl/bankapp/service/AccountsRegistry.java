package pl.bankapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bankapp.dto.PersonalAccountPartialUpdateDTO;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.entity.PersonalAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountsRegistry {

    /**
     * Feature 14 - Accounts Registry(Rejestr kont) dla kont osobistych
     * Feature 14 - przechowywać konta osobiste w liście
     */
    private List<PersonalAccount> accounts = new ArrayList<>();

    /**
     * Feature 14 - pozwoli na dodawanie konta do tej listy
     */
    public PersonalAccount addAccount(PersonalAccount account) {
        for (PersonalAccount p : accounts) {
            if (p.getIdentification().equals(account.getIdentification())) {
                throw new IllegalArgumentException("Pesel: " + account.getIdentification() + "already in account registry.");
            }
        }
        accounts.add(account);
        return account;
    }

    /**
     * Feature 14 - pozwoli na wyszukanie konta za pomocą peselu
     */
    public PersonalAccount findAccountByPesel(String pesel) {
        return accounts
                .stream()
                .filter(p -> p.getIdentification().equals(pesel))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No Account with provided pesel: " + pesel));
    }

    /**
     * Feature 14 - zwróci wszystkie konta
     */
    public List<PersonalAccount> getAllAccounts() {
        return accounts;
    }

    /**
     * Feature 14 - zwróci ilość kont w rejestrze
     */
    public int getAmountOfAccounts() {
        return accounts.size();
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    public boolean deleteAccount(String pesel) {
        return accounts.removeIf(a -> pesel.equals(a.getIdentification()));
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    public PersonalAccount updatePersonalAccount(String pesel, PersonalAccount personalAccount) {
        PersonalAccount foundAccount = findAccountByPesel(pesel);
        foundAccount.setName(personalAccount.getName());
        foundAccount.setSurname(personalAccount.getSurname());
        return foundAccount;
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    public PersonalAccount partialUpdatePersonalAccount(String pesel, PersonalAccountPartialUpdateDTO updateDTO) {
        PersonalAccount account = findAccountByPesel(pesel);
        if (updateDTO.getName() != null) {
            account.setName(updateDTO.getName());
        }
        if (updateDTO.getSurname() != null) {
            account.setSurname(updateDTO.getSurname());
        }
        return account;
    }

    /**
     * Feature 17 - przelewy przez API
     */
    public void createTransfer(String pesel, TransferDTO transferDTO) {
        if (transferDTO == null) {
            throw new IllegalArgumentException("Provide transfer request");
        }
        PersonalAccount foundAccount = findAccountByPesel(pesel);
        double amount = transferDTO.getAmount();
        switch (transferDTO.getType()) {
            case EXPRESS -> foundAccount.expressOutgoingTransfer(amount);
            case INCOMING -> foundAccount.incomingTransfer(amount);
            case OUTGOING -> foundAccount.outgoingTransfer(amount);
            case null, default -> throw new IllegalArgumentException("Wrong type of transfer");
        }
    }

    // Feature 4 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego.
//    public void addAccountWithPromoCode(PersonalAccount personalAccount, String promoCode) {
//        boolean exists = accounts.stream()
//                .anyMatch(p -> p.getIdentification()
//                        .equals(personalAccount.getIdentification()));
//        if (!exists) {
//            accounts.add(personalAccount);
//            personalAccount.usePromoCode(promoCode);
//        }
//    }
}
