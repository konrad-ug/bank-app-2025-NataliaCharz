package pl.bankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonalAccountPartialUpdateDTO {
    private String name;
    private String surname;

}
