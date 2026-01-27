package pl.bankapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.bankapp.entity.TransferType;

@AllArgsConstructor
@Getter
@Setter
public class TransferDTO {
    private double amount;
    private TransferType type;

}
