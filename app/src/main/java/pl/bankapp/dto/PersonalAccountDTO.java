package pl.bankapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */
@Getter
@Setter
public class PersonalAccountDTO {
    public String name;
    public String surname;
    public String pesel;
    public String promoCode;
    public double balance;

    public PersonalAccountDTO(String name, String surname, String pesel) {
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
    }
}
