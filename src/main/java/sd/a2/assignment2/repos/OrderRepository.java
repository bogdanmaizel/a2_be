package sd.a2.assignment2.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sd.a2.assignment2.domain.Order;

import java.util.List;
import java.util.Set;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o where o.customer.id=?1")
    List<Order> findByCustomerId(Long id);

    List<Order> findByRestaurantId(Long id);

}
