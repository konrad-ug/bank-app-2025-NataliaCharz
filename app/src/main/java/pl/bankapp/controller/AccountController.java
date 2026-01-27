package pl.bankapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.dto.PersonalAccountPartialUpdateDTO;
import pl.bankapp.dto.TransferDTO;
import pl.bankapp.exception.OutgoingTransactionFailedException;
import pl.bankapp.mapper.PersonalAccountMapper;
import pl.bankapp.service.AccountsRegistry;
import pl.bankapp.validator.PeselValidator;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;
import static pl.bankapp.controller.AccountController.ACCOUNT_URL;


@RestController
@RequestMapping(ACCOUNT_URL)
@RequiredArgsConstructor
public class AccountController {

    public final static String ACCOUNT_URL = "/api/accounts";

    private final AccountsRegistry accountsRegistry;
    private final PersonalAccountMapper personalAccountMapper;

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

    @GetMapping
    public List<PersonalAccountDTO> getAllPersonalAccounts() {
        return accountsRegistry.getAllAccounts()
                .stream()
                .map(personalAccountMapper::personalAccountToDTO)
                .collect(toList());
    }

    @GetMapping("/count")
    public Map<String, Integer> getNumberOfPersonalAccounts() {
        return Map.of("count", accountsRegistry.getAmountOfAccounts());
    }

    @GetMapping("/{pesel}")
    public PersonalAccountDTO getPersonalAccountByPesel(@PathVariable String pesel) {
        try {
            return personalAccountMapper.personalAccountToDTO(accountsRegistry.findAccountByPesel(pesel));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePersonalAccount(@PathVariable String pesel, @RequestBody PersonalAccountDTO personalAccount) {
        try {
            accountsRegistry.updatePersonalAccount(pesel, personalAccountMapper.dtoToPersonalAccount(personalAccount));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{pesel}")
    @ResponseStatus(HttpStatus.OK)
    public void partialUpdatePersonalAccount(@PathVariable String pesel, @RequestBody PersonalAccountPartialUpdateDTO partialUpdateDTO) {
        try {
            accountsRegistry.partialUpdatePersonalAccount(pesel, partialUpdateDTO);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{pesel}")
    public void deletePersonalAccount(@PathVariable String pesel) {
        boolean deleted = accountsRegistry.deleteAccount(pesel);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

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