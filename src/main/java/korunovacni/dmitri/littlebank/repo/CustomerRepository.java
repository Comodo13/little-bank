package korunovacni.dmitri.littlebank.repo;

import korunovacni.dmitri.littlebank.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
