package pl.bankapp.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.bankapp.service.NipValidator;

@Service
@RequiredArgsConstructor
public class CompanyAccountService {

    private final NipValidator nipValidator;

    public CompanyAccount createCompanyAccount(String companyName, String nip) {
        nipValidator.validateNipOrThrow(nip);
        return new CompanyAccount(companyName, nip);
    }
}
