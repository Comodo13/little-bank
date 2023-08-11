package korunovacni.dmitri.littlebank.domain;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;

@Entity
@Data
@Table(name = "bank_accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String IBAN;
    private Currency currency;
    private BigDecimal balance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
