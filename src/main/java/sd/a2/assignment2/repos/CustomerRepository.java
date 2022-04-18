package sd.a2.assignment2.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.a2.assignment2.domain.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
