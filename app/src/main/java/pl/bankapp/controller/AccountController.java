package pl.bankapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.dto.PersonalAccountPartialUpdateDTO;
import pl.bankapp.dto.PersonalAccountResponse;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.mapper.PersonalAccountMapper;
import pl.bankapp.mapper.PersonalAccountResponseMapper;
import pl.bankapp.service.AccountsRegistry;
import pl.bankapp.validator.PeselValidator;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;
import static pl.bankapp.controller.AccountController.ACCOUNT_URL;

/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */
@RestController
@RequestMapping(ACCOUNT_URL)
@RequiredArgsConstructor
public class AccountController {

    public final static String ACCOUNT_URL = "/api/accounts";

    private final AccountsRegistry accountsRegistry;
    private final PersonalAccountMapper personalAccountMapper;
    private final PersonalAccountResponseMapper personalAccountResponseMapper;

    /**
     * Feature 15 - stworzy konto osobiste i doda je do rejestru.
     * Feature 16 - unikatowy pesel HTTP status 409 w przypadku próby dodania konta z pesel'em który już istnieje w rejestrze
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createPersonalAccount(@RequestBody PersonalAccountDTO personalAccount) {
        try {
            PeselValidator.validatePesel(personalAccount.getPesel());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try {
            accountsRegistry.addAccount(personalAccountMapper.dtoToPersonalAccount(personalAccount));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    //Feature 4 - zastosowanie kodu promocyjnego przy zakładaniu konta osobistego.
//    @PostMapping("/{promoCode}")
//    public void createPersonalAccountWithPromoCode(@RequestBody PersonalAccountDTO personalAccount, @PathVariable String promoCode) {
//        try {
//            PeselValidator.validatePesel(personalAccount.getPesel());
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            accountsRegistry.addAccountWithPromoCode(personalAccountMapper.dtoToPersonalAccount(personalAccount), personalAccount.getPromoCode());
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.CONFLICT);
//        }
//    }

    /**
     * Feature 15 - zwraca wszystkie konta, lub pustą tablicę jeżeli kont nie ma
     */
    @GetMapping
    public List<PersonalAccountDTO> getAllPersonalAccounts() {
        return accountsRegistry.getAllAccounts()
                .stream()
                .map(personalAccountMapper::personalAccountToDTO)
                .collect(toList());
    }

    /**
     * Feature 15 - zwraca ilość kont zapisanych w rejestrze. Spring serializuje Map na JSON
     */
    @GetMapping("/count")
    public Map<String, Integer> getNumberOfPersonalAccounts() {
        return Map.of("count", accountsRegistry.getAmountOfAccounts());
    }

    /**
     * Feature 15 - zwróci dane konta (imię, nazwisko, pesel, saldo) z
     * podanym peselm. Jeżeli konta z podanym peselem nie ma w rejestrze zwracamy 404
     */
    @GetMapping("/{pesel}")
    public PersonalAccountResponse getPersonalAccountByPesel(@PathVariable String pesel) {
        try {
            return personalAccountResponseMapper.toResponse(accountsRegistry.findAccountByPesel(pesel));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    @PutMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePersonalAccount(@PathVariable String pesel, @RequestBody PersonalAccountDTO personalAccount) {
        try {
            accountsRegistry.updatePersonalAccount(pesel, personalAccountMapper.dtoToPersonalAccount(personalAccount));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    @PatchMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public void partialUpdatePersonalAccount(@PathVariable String pesel, @RequestBody PersonalAccountPartialUpdateDTO partialUpdateDTO) {
        try {
            accountsRegistry.partialUpdatePersonalAccount(pesel, partialUpdateDTO);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Feature 15 - pozostałe metody CRUD
     */
    @DeleteMapping("/{pesel}")
    public void deletePersonalAccount(@PathVariable String pesel) {
        boolean deleted = accountsRegistry.deleteAccount(pesel);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Feature 17 - przelewy przez API. Stworzone DTO TransferDTO oraz enum TransferType
     * 404 - konto o podanym pesel nie istnieje
     * 400 - zły typ transferu
     * 200 - przelew zaakceptowany
     * 422 - dla przelewów wychodzących, gdy transakcja się nie udała
     */
    @PostMapping("/{pesel}/transfer")
    public ResponseEntity<String> createTransferForPersonalAccount(@PathVariable String pesel, @RequestBody TransferDTO transferDTO) {
        try {
            accountsRegistry.createTransfer(pesel, transferDTO);
            return ResponseEntity.ok("The order has been accepted for execution");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (OutgoingTransactionFailedException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}