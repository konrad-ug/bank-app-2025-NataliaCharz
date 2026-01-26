package pl.bankapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.mapper.PersonalAccountMapper;
import pl.bankapp.service.AccountsRegistry;
import pl.bankapp.service.PersonalAccountService;
import pl.bankapp.validator.PeselValidator;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller("/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final PersonalAccountService personalAccountService;
    private final PersonalAccountMapper personalAccountMapper;
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
