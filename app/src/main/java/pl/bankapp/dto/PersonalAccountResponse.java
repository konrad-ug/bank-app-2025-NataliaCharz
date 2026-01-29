package pl.bankapp.dto;

/**
 * Feature 15 - Mikroserwisy - Kontroler REST dla kont osobistych
 */
public record PersonalAccountResponse(String name, String surname, String pesel, double balance) {
}
