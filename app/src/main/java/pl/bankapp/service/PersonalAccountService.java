package pl.bankapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bankapp.entity.HistoryTransfer;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.repository.PersonalAccountRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalAccountService {
    private final PersonalAccountRepository personalAccountRepository;

    public void dumpAccountsToDatabase(List<PersonalAccount> accounts) {
        personalAccountRepository.deleteAll();
        for (PersonalAccount account : accounts) {
            for (Double amount : account.getHistory()) {
                HistoryTransfer transfer = new HistoryTransfer();
                transfer.setAmount(amount);
                transfer.setDate(LocalDate.now());
                transfer.setType(amount >= 0 ? "INCOMING" : "OUTGOING");
                transfer.setAccount(account);
                account.getHistoryTransfers().add(transfer);
            }
            personalAccountRepository.save(account);
        }
    }

    public List<PersonalAccount> loadAccountsFromDatabase() {
        return personalAccountRepository.findAll();
    }

}
