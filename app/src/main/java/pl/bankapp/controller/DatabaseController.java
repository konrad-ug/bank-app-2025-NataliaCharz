package pl.bankapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.service.AccountsRegistry;
import pl.bankapp.service.PersonalAccountService;

import java.util.List;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final PersonalAccountService personalAccountService;
    private final AccountsRegistry accountsRegistry;

    @PostMapping("/dump")
    public void dumpRegistryToDatabase() {
        personalAccountService.dumpAccountsToDatabase(accountsRegistry.getAllAccounts());
    }

    @PostMapping("/load")
    public void loadDatabaseToRegistry() {
        accountsRegistry.getAllAccounts().clear();
        List<PersonalAccount> accounts = personalAccountService.loadAccountsFromDatabase();
        accounts.forEach(accountsRegistry::addAccount);
    }
}
