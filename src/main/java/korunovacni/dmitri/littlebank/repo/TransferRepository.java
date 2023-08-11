package korunovacni.dmitri.littlebank.repo;

import korunovacni.dmitri.littlebank.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByDebtorIBAN(String IBAN);

    List<Transfer> findByCreditorIBAN(String IBAN);

    List<Transfer> findByAmount(BigDecimal amount);

    List<Transfer> findByMessage(String message);

}
