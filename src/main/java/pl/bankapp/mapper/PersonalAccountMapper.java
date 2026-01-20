package pl.bankapp.mapper;

import org.springframework.stereotype.Component;
import pl.bankapp.dto.PersonalAccountDTO;
import pl.bankapp.entity.PersonalAccount;

@Component
public class PersonalAccountMapper {

    public PersonalAccountDTO personalAccountToDTO(PersonalAccount personalAccount) {
        return new PersonalAccountDTO(
                personalAccount.getName(),
                personalAccount.getSurname(),
                personalAccount.getIdentification()
        );
    }

    public PersonalAccount dtoToPersonalAccount(PersonalAccountDTO personalAccountDTO) {
        return new PersonalAccount(
                personalAccountDTO.getName(),
                personalAccountDTO.getSurname(),
                personalAccountDTO.getPesel()
        );
    }
}
