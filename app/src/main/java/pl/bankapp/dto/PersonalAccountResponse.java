package pl.bankapp.dto;

public record PersonalAccountResponse(String name, String surname, String pesel, double balance) {
}
