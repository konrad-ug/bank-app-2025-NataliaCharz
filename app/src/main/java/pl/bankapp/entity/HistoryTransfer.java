package pl.bankapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class HistoryTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    private String type;
    @Column(name = "amount")
    private double amount;
    @Column(name = "date")
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private PersonalAccount account;
}
