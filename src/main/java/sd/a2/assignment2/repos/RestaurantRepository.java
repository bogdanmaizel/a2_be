package sd.a2.assignment2.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import sd.a2.assignment2.domain.Restaurant;

import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByAdminId(Long id);
}
