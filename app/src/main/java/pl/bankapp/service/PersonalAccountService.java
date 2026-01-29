package pl.bankapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bankapp.entity.HistoryTransaction;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.repository.PersonalAccountRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Feature 20 - zrzuta rejestru kont
 */
@Service
@RequiredArgsConstructor
public class PersonalAccountService {
    private final PersonalAccountRepository personalAccountRepository;

    @Transactional
    public void registryToDatabase(List<PersonalAccount> accounts) {
        personalAccountRepository.deleteAll();
        for (PersonalAccount account : accounts) {
            for (Double amount : account.getHistory()) {
                HistoryTransaction transfer = new HistoryTransaction();
                transfer.setAmount(amount);
                transfer.setDate(LocalDate.now());
                transfer.setType(amount >= 0 ? "INCOMING" : "OUTGOING");
                transfer.setAccount(account);
                account.getHistoryTransactions().add(transfer);
            }
            personalAccountRepository.save(account);
        }
    }

    public List<PersonalAccount> databaseToRegistry() {
        return personalAccountRepository.findAll();
    }

}
