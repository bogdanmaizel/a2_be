package sd.a2.assignment2.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sd.a2.assignment2.domain.Food;

import java.util.List;


public interface FoodRepository extends JpaRepository<Food, Long> {

    @Query("select f from Food f where f.restaurant.id = ?1")
    List<Food> findByRestaurantId(Long id);

}
