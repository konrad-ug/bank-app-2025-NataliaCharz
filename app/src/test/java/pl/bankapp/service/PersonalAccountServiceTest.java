package pl.bankapp.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.bankapp.entity.HistoryTransaction;
import pl.bankapp.entity.PersonalAccount;
import pl.bankapp.repository.PersonalAccountRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@Tag("unit")
public class PersonalAccountServiceTest {

    @Mock
    private PersonalAccountRepository personalAccountRepository;

    @InjectMocks
    private PersonalAccountService personalAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void dumpAccountsToDatabase_shouldSaveAccountsWithHistory() {
        // given
        PersonalAccount account = new PersonalAccount("Joe", "Doe", "12345678909");
        account.incomingTransfer(150.0);
        account.outgoingTransfer(50.0);
        List<PersonalAccount> accounts = List.of(account);

        // when
        personalAccountService.dumpAccountsToDatabase(accounts);

        // then
        verify(personalAccountRepository, times(1)).deleteAll();
        verify(personalAccountRepository, times(1)).save(account);

        assertEquals(2, account.getHistoryTransactions().size());
        HistoryTransaction t1 = account.getHistoryTransactions().get(0);
        HistoryTransaction t2 = account.getHistoryTransactions().get(1);
        assertEquals("INCOMING", t1.getType());
        assertEquals("OUTGOING", t2.getType());
        assertEquals(LocalDate.now(), t1.getDate());
    }

    @Test
    void loadAccountsFromDatabase_shouldReturnAllAccounts() {
        // given
        List<PersonalAccount> mockAccounts = List.of(
                new PersonalAccount("Natalia", "Charz", "12345678909")
        );
        when(personalAccountRepository.findAll()).thenReturn(mockAccounts);

        // when
        List<PersonalAccount> result = personalAccountService.loadAccountsFromDatabase();

        // then
        assertEquals(1, result.size());
        assertEquals("Natalia", result.get(0).getName());
        verify(personalAccountRepository, times(1)).findAll();
    }
}

