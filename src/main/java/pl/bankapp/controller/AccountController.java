package pl.bankapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.entity.TransferRequest;
import pl.bankapp.exception.IncomingTransactionFailedException;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.service.AccountsRegistry;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.ResponseEntity.ok;
import static pl.bankapp.controller.AccountController.ACCOUNT_URL;


@RestController
@RequestMapping(ACCOUNT_URL)
@RequiredArgsConstructor
public class AccountController {

    public final static String ACCOUNT_URL = "/api/accounts";

    private final AccountsRegistry accountsRegistry;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public PersonalAccount createPersonalAccount(@RequestBody PersonalAccount personalAccount) {
        try {
            accountsRegistry.addAccount(personalAccount);
            return personalAccount;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    public List<PersonalAccount> getAllPersonalAccounts() {
        return accountsRegistry.getAllAccounts();
    }

    @GetMapping("/count")
    public Map<String, Integer> getNumberOfPersonalAccounts() {
        return Map.of("count", accountsRegistry.getAmountOfAccounts());
    }

    @GetMapping("/{pesel}")
    public PersonalAccount getPersonalAccountByPesel(@PathVariable String pesel) {
        try {
            return accountsRegistry.findAccountByPesel(pesel);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public PersonalAccount updatePersonalAccount(@PathVariable String pesel, @RequestBody PersonalAccount personalAccount) {
        try {
            return accountsRegistry.updatePersonalAccount(pesel, personalAccount);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public PersonalAccount partialUpdatePersonalAccount(@PathVariable String pesel, @RequestBody String... args) {
        try {
            return accountsRegistry.partialUpdatePersonalAccount(pesel, args);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{pesel}")
    public ResponseEntity<Void> deletePersonalAccount(@PathVariable String pesel) {
        boolean deleted = accountsRegistry.deleteAccount(pesel);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ok().build();
    }

    @PostMapping("/{pesel}/transfer")
    public ResponseEntity<String> createTransferForPersonalAccount(@PathVariable String pesel, @RequestBody TransferRequest transferRequest) {
        try {
            accountsRegistry.createTransfer(pesel, transferRequest);
            return ResponseEntity.ok("The order has been accepted for execution");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (OutgoingTransactionFailedException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}