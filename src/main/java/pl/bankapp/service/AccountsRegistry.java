package pl.bankapp.service;

import org.springframework.stereotype.Service;
import pl.bankapp.entity.PersonalAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AccountsRegistry {
    private List<PersonalAccount> accounts = new ArrayList<>();

    public PersonalAccount addAccount(PersonalAccount account) {
        for (PersonalAccount p : accounts) {
            if (p.getIdentification().equals(account.getIdentification())) {
                throw new IllegalArgumentException("Pesel: " + account.getIdentification() + "already used for creating an account");
            }
        }
        accounts.add(account);
        return account;
    }

    public PersonalAccount findAccountByPesel(String pesel) {
        return accounts
                .stream()
                .filter(p -> p.getIdentification().equals(pesel))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No Account with provided pesel: " + pesel));
    }

    public List<PersonalAccount> getAllAccounts() {
        return accounts;
    }

    public int getAmountOfAccounts() {
        return accounts.size();
    }

    public boolean deleteAccount(String pesel) {
        return accounts.removeIf(a -> pesel.equals(a.getIdentification()));
    }

    public PersonalAccount updatePersonalAccount(String pesel, PersonalAccount personalAccount) {
        PersonalAccount foundAccount = findAccountByPesel(pesel);
        foundAccount.setName(personalAccount.getName());
        foundAccount.setSurname(personalAccount.getSurname());
        return foundAccount;
    }

    public PersonalAccount partialUpdatePersonalAccount(String pesel, String... args) {
        PersonalAccount account = findAccountByPesel(pesel);
        if (args.length > 0 && args[0] != null) {
            account.setName(args[0]);
        }
        if (args.length > 1 && args[1] != null) {
            account.setSurname(args[1]);
        }
        return account;
    }
}
