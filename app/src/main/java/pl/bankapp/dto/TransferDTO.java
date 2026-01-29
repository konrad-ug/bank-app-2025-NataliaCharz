package pl.bankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.bankapp.entity.TransferType;

@AllArgsConstructor
@Getter
@Setter
public class TransferDTO {
    //Feature 19 - przelewy przez API - DTO dla przelewów
    private double amount;
    private TransferType type;

}
