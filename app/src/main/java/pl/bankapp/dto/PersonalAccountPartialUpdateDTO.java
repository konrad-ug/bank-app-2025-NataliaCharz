package pl.bankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalAccountPartialUpdateDTO {
    private String name;
    private String surname;

}
