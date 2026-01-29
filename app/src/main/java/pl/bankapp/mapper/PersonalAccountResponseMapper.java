package pl.bankapp.mapper;

import org.springframework.stereotype.Component;
import pl.bankapp.dto.PersonalAccountResponse;
import pl.bankapp.entity.PersonalAccount;


/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */
@Component
public class PersonalAccountResponseMapper {
    public PersonalAccountResponse toResponse(PersonalAccount account) {
        return new PersonalAccountResponse(
                account.getName(),
                account.getSurname(),
                account.getIdentification(),
                account.getBalance()
        );
    }
}
