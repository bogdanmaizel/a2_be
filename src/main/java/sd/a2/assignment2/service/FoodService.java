package sd.a2.assignment2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Food;
import sd.a2.assignment2.domain.Restaurant;
import sd.a2.assignment2.model.FoodDTO;
import sd.a2.assignment2.repos.FoodRepository;
import sd.a2.assignment2.repos.RestaurantRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles various logic and operations concerning the food entities.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    /**
     * Fetch all the foods currently in the database.
     * @return A list of all the foods.
     */

    public List<FoodDTO> findAll() {
        log.info("Fetching food list");
        return foodRepository.findAll()
                .stream()
                .map(food -> mapToDTO(food, new FoodDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Search for a food by its ID.
     * @param id The food ID.
     * @return The corresponding food, mapped to a DTO.
     */

    public FoodDTO get(final Long id) {
        log.info("Fetching food with ID {}", id);
        return foodRepository.findById(id)
                .map(food -> mapToDTO(food, new FoodDTO()))
                .orElseThrow(() -> {
                    log.warn("Food with ID {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found");
                });
    }

    /**
     * Fetch the menu of a restaurant.
     * @param id The ID of the restaurant.
     * @return The list of foods associated to the restaurant.
     */

    public List<FoodDTO> getMenu(final Long id) {
        log.info("Fetching menu for restaurant {}", id);
        return foodRepository.findByRestaurantId(id).stream()
                .map(food -> mapToDTO(food, new FoodDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new food entity, based on the parameters given in the DTO.
     * @param foodDTO the DTO containing the information about the food object.
     * @return A DTO reflecting the received information, completed with an ID.
     */

    public FoodDTO create(final FoodDTO foodDTO) { // builder pattern
        log.info("Adding food item - {}", foodDTO.getName());
        final Food food = Food.builder()
                .name(foodDTO.getName())
                .description(foodDTO.getDescription())
                .price(foodDTO.getPrice())
                .category(foodDTO.getCategory())
                .restaurant(restaurantRepository.getById(foodDTO.getRestaurant()))
                .build();
        mapToEntity(foodDTO, food);
        foodDTO.setId(foodRepository.save(food).getId());
        return foodDTO;
    }

    public void update(final Long id, final FoodDTO foodDTO) {
        final Food food = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(foodDTO, food);
        foodRepository.save(food);
    }

    public void delete(final Long id) {
        foodRepository.deleteById(id);
    }

    /**
     * Mapper method: creates a DTO from an entity.
     * @param food The entity to be converted.
     * @param foodDTO The resulting DTO.
     * @return The resulting DTO.
     */

    private FoodDTO mapToDTO(final Food food, final FoodDTO foodDTO) {
        log.info("Creating DTO for food {}", food.getName());
        foodDTO.setId(food.getId());
        foodDTO.setName(food.getName());
        foodDTO.setDescription(food.getDescription());
        foodDTO.setPrice(food.getPrice());
        foodDTO.setCategory(food.getCategory());
        foodDTO.setRestaurant(food.getRestaurant() == null ? null : food.getRestaurant().getId());
        return foodDTO;
    }

    /**
     * Mapper method: creates an entity from a given DTO
     * @param foodDTO The DTO to be converted.
     * @param food The resulting entity.
     * @return The resulting entity.
     */

    private Food mapToEntity(final FoodDTO foodDTO, final Food food) {
        log.info("Creating entity for food {}", foodDTO.getName());
        food.setName(foodDTO.getName());
        food.setDescription(foodDTO.getDescription());
        food.setPrice(foodDTO.getPrice());
        food.setCategory(foodDTO.getCategory());
        if (foodDTO.getRestaurant() != null && (food.getRestaurant() == null || !food.getRestaurant().getId().equals(foodDTO.getRestaurant()))) {
            final Restaurant restaurant = restaurantRepository.findById(foodDTO.getRestaurant())
                    .orElseThrow(() -> {
                        log.warn("Restaurant with ID {} not found", foodDTO.getRestaurant());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
                    });
            food.setRestaurant(restaurant);
        }
        return food;
    }

}
