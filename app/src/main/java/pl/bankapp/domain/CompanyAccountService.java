package pl.bankapp.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bankapp.service.NipValidator;

/**
 * Feature 18 - serwis do tworzenia konta firmowego z walidacją NIP
 * Oddzielenie wstrzykiwania serwisu NipValidator od logiki tworzenia konta firmowego.
 * Konstruktor CompanyAccount jest dostępny jedynie w paczce. Nie można stworzyć inaczej konta, niż przez CompanyAccountService.
 */
@Service
@RequiredArgsConstructor
public class CompanyAccountService {

    private final NipValidator nipValidator;

    public CompanyAccount createCompanyAccount(String companyName, String nip) {
        nipValidator.validateNipOrThrow(nip);
        return new CompanyAccount(companyName, nip);
    }
}
