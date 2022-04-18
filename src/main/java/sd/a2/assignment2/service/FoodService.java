package sd.a2.assignment2.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Food;
import sd.a2.assignment2.domain.Restaurant;
import sd.a2.assignment2.model.FoodDTO;
import sd.a2.assignment2.repos.FoodRepository;
import sd.a2.assignment2.repos.RestaurantRepository;


@Service
public class FoodService {

    private final FoodRepository foodRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public FoodService(final FoodRepository foodRepository,
                       final RestaurantRepository restaurantRepository) {
        this.foodRepository = foodRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<FoodDTO> findAll() {
        return foodRepository.findAll()
                .stream()
                .map(food -> mapToDTO(food, new FoodDTO()))
                .collect(Collectors.toList());
    }

    public FoodDTO get(final Long id) {
        return foodRepository.findById(id)
                .map(food -> mapToDTO(food, new FoodDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<FoodDTO> getMenu(final Long id) {
        return foodRepository.findByRestaurantId(id).stream()
                .map(food -> mapToDTO(food, new FoodDTO()))
                .collect(Collectors.toList());
    }

    public FoodDTO create(final FoodDTO foodDTO) { // builder pattern
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

    private FoodDTO mapToDTO(final Food food, final FoodDTO foodDTO) {
        foodDTO.setId(food.getId());
        foodDTO.setName(food.getName());
        foodDTO.setDescription(food.getDescription());
        foodDTO.setPrice(food.getPrice());
        foodDTO.setCategory(food.getCategory());
        foodDTO.setRestaurant(food.getRestaurant() == null ? null : food.getRestaurant().getId());
        return foodDTO;
    }

    private Food mapToEntity(final FoodDTO foodDTO, final Food food) {
        food.setName(foodDTO.getName());
        food.setDescription(foodDTO.getDescription());
        food.setPrice(foodDTO.getPrice());
        food.setCategory(foodDTO.getCategory());
        if (foodDTO.getRestaurant() != null && (food.getRestaurant() == null || !food.getRestaurant().getId().equals(foodDTO.getRestaurant()))) {
            final Restaurant restaurant = restaurantRepository.findById(foodDTO.getRestaurant())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "restaurant not found"));
            food.setRestaurant(restaurant);
        }
        return food;
    }

}
