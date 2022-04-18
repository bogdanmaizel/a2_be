package sd.a2.assignment2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Restaurant;
import sd.a2.assignment2.domain.RestaurantAdmin;
import sd.a2.assignment2.model.FoodDTO;
import sd.a2.assignment2.model.RestaurantDTO;
import sd.a2.assignment2.repos.RestaurantAdminRepository;
import sd.a2.assignment2.repos.RestaurantRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantAdminRepository restaurantAdminRepository;

    @Autowired
    public RestaurantService(final RestaurantRepository restaurantRepository,
                             final RestaurantAdminRepository restaurantAdminRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantAdminRepository = restaurantAdminRepository;
    }

    public List<RestaurantDTO> findAll() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> mapToDTO(restaurant, new RestaurantDTO()))
                .collect(Collectors.toList());
    }

    public RestaurantDTO get(final Long id) {
        return restaurantRepository.findById(id)
                .map(restaurant -> mapToDTO(restaurant, new RestaurantDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public RestaurantDTO getFromAdmin(final Long adminId) {
        return restaurantRepository.findByAdminId(adminId)
                .map(r -> mapToDTO(r, new RestaurantDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public RestaurantDTO create(final RestaurantDTO restaurantDTO) {
        final Restaurant restaurant = new Restaurant();
        mapToEntity(restaurantDTO, restaurant);
        restaurantDTO.setId(restaurantRepository.save(restaurant).getId());
        return restaurantDTO;
    }

    public void update(final Long id, final RestaurantDTO restaurantDTO) {
        final Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(restaurantDTO, restaurant);
        restaurantRepository.save(restaurant);
    }

    public void delete(final Long id) {
        restaurantRepository.deleteById(id);
    }

    private RestaurantDTO mapToDTO(final Restaurant restaurant, final RestaurantDTO restaurantDTO) {
        restaurantDTO.setId(restaurant.getId());
        restaurantDTO.setName(restaurant.getName());
        restaurantDTO.setLocation(restaurant.getLocation());
        restaurantDTO.setZones(restaurant.getZones());
        restaurantDTO.setAdmin(restaurant.getAdmin() == null ? null : restaurant.getAdmin().getId());
        return restaurantDTO;
    }

    private Restaurant mapToEntity(final RestaurantDTO restaurantDTO, final Restaurant restaurant) {
        restaurant.setName(restaurantDTO.getName());
        restaurant.setLocation(restaurantDTO.getLocation());
        restaurant.setZones(restaurantDTO.getZones());
        if (restaurantDTO.getAdmin() != null && (restaurant.getAdmin() == null || !restaurant.getAdmin().getId().equals(restaurantDTO.getAdmin()))) {
            final RestaurantAdmin admin = restaurantAdminRepository.findById(restaurantDTO.getAdmin())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "admin not found"));
            restaurant.setAdmin(admin);
        }
        return restaurant;
    }

}
