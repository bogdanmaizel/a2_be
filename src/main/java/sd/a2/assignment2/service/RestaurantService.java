package sd.a2.assignment2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Restaurant;
import sd.a2.assignment2.domain.RestaurantAdmin;
import sd.a2.assignment2.model.RestaurantDTO;
import sd.a2.assignment2.repos.RestaurantAdminRepository;
import sd.a2.assignment2.repos.RestaurantRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles restaurant logic.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantAdminRepository restaurantAdminRepository;

    /**
     * @return A list of restaurants.
     */

    public List<RestaurantDTO> findAll() {
        log.info("Fetching all restaurants");
        return restaurantRepository.findAll()
                .stream()
                .map(restaurant -> mapToDTO(restaurant, new RestaurantDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Fetch a single restaurant.
     * @param id The restaurant ID.
     * @return The referenced restaurant.
     */

    public RestaurantDTO get(final Long id) {
        log.info("Fetching restaurant {}", id);
        return restaurantRepository.findById(id)
                .map(restaurant -> mapToDTO(restaurant, new RestaurantDTO()))
                .orElseThrow(() -> {
                    log.warn("Restaurant with ID {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
                });
    }

    /**
     * @param adminId The admin's ID.
     * @return The corresponding restaurant.
     */

    public RestaurantDTO getFromAdmin(final Long adminId) {
        log.info("Fetching restaurant with admin {}", adminId);
        return restaurantRepository.findByAdminId(adminId)
                .map(r -> mapToDTO(r, new RestaurantDTO()))
                .orElseThrow(() -> {
                    log.warn("Restaurant admin with ID {} not found", adminId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant admin not found");
                });
    }

    /**
     * Create a new restaurant entity.
     * @param restaurantDTO The DTO containing the restaurant information.
     * @return A DTO containing the saved information.
     */

    public RestaurantDTO create(final RestaurantDTO restaurantDTO) {
        log.info("Creating new restaurant for admin {}", restaurantDTO.getAdmin());
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

    /**
     * Mapper method, creates a DTO from a given entity.
     * @param restaurant The given entity.
     * @param restaurantDTO The resulting DTO.
     * @return The resulting DTO.
     */

    private RestaurantDTO mapToDTO(final Restaurant restaurant, final RestaurantDTO restaurantDTO) {
        log.info("Mapping restaurant {} to DTO", restaurant.getId());
        restaurantDTO.setId(restaurant.getId());
        restaurantDTO.setName(restaurant.getName());
        restaurantDTO.setLocation(restaurant.getLocation());
        restaurantDTO.setZones(restaurant.getZones());
        restaurantDTO.setAdmin(restaurant.getAdmin() == null ? null : restaurant.getAdmin().getId());
        return restaurantDTO;
    }

    /**
     * Mapper method, creates an entity from a given DTO
     * @param restaurantDTO The given DTO.
     * @param restaurant The resulting entity.
     * @return The resulting entity.
     */

    private Restaurant mapToEntity(final RestaurantDTO restaurantDTO, final Restaurant restaurant) {
        log.info("Creating entity from DTO for restaurant {}", restaurantDTO.getName());
        restaurant.setName(restaurantDTO.getName());
        restaurant.setLocation(restaurantDTO.getLocation());
        restaurant.setZones(restaurantDTO.getZones());
        if (restaurantDTO.getAdmin() != null && (restaurant.getAdmin() == null || !restaurant.getAdmin().getId().equals(restaurantDTO.getAdmin()))) {
            final RestaurantAdmin admin = restaurantAdminRepository.findById(restaurantDTO.getAdmin())
                    .orElseThrow(() -> {
                        log.warn("Restaurant admin with ID {} not found", restaurantDTO.getAdmin());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant admin not found");
                    });
            restaurant.setAdmin(admin);
        }
        return restaurant;
    }

}
