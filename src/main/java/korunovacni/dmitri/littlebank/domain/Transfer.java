package korunovacni.dmitri.littlebank.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bank_transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private BigDecimal amount;
    @Column(name = "debtor_iban")
    private String debtorIBAN;
    @Column(name = "creditor_iban")
    private String creditorIBAN;
    private String message;

}
