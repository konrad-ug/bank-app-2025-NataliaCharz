package pl.bankapp.mapper;

import org.springframework.stereotype.Component;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.entity.PersonalAccount;


/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */
@Component
public class PersonalAccountMapper {

    public PersonalAccountDTO personalAccountToDTO(PersonalAccount personalAccount) {
        PersonalAccountDTO personalAccountDTO = new PersonalAccountDTO(
                personalAccount.getName(),
                personalAccount.getSurname(),
                personalAccount.getIdentification()
        );
        personalAccountDTO.setBalance(personalAccount.getBalance());
        personalAccountDTO.setPromoCode(personalAccount.getPromoCode());
        return personalAccountDTO;
    }

    public PersonalAccount dtoToPersonalAccount(PersonalAccountDTO personalAccountDTO) {
        PersonalAccount personalAccount = new PersonalAccount(personalAccountDTO.getName(),
                personalAccountDTO.getSurname(),
                personalAccountDTO.getPesel());
        personalAccount.setBalance(personalAccountDTO.getBalance());
        personalAccount.setPromoCode(personalAccountDTO.getPromoCode());
        return personalAccount;
    }
}
