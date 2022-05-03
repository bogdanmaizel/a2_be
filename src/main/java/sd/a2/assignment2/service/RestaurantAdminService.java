package sd.a2.assignment2.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.RestaurantAdmin;
import sd.a2.assignment2.domain.User;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.repos.RestaurantAdminRepository;
import sd.a2.assignment2.repos.UserRepository;

/**
 * Handles various logic and operations concerning the restaurant admins.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantAdminService {

    private final RestaurantAdminRepository restaurantAdminRepository;
    private final UserRepository userRepository;

    public List<RestaurantAdminDTO> findAll() {
        return restaurantAdminRepository.findAll()
                .stream()
                .map(restaurantAdmin -> mapToDTO(restaurantAdmin, new RestaurantAdminDTO()))
                .collect(Collectors.toList());
    }

    /**
     * @param id The restaurant admin ID.
     * @return The restaurant admin data.
     */

    public RestaurantAdminDTO get(final Long id) {
        log.info("Fetching restaurant admin {}", id);
        return restaurantAdminRepository.findById(id)
                .map(restaurantAdmin -> mapToDTO(restaurantAdmin, new RestaurantAdminDTO()))
                .orElseThrow(() -> {
                    log.warn("Restaurant admin with ID {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant admin not found");
                });
    }

    /**
     * @param restaurantAdminDTO Restaurant admin data
     * @return The ID of the new restaurant admin entity.
     */

    public Long create(final RestaurantAdminDTO restaurantAdminDTO) {
        log.info("Creating new restaurant admin for user {}", restaurantAdminDTO.getUser());
        final RestaurantAdmin restaurantAdmin = new RestaurantAdmin();
        mapToEntity(restaurantAdminDTO, restaurantAdmin);
        return restaurantAdminRepository.save(restaurantAdmin).getId();
    }

    public void update(final Long id, final RestaurantAdminDTO restaurantAdminDTO) {
        final RestaurantAdmin restaurantAdmin = restaurantAdminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(restaurantAdminDTO, restaurantAdmin);
        restaurantAdminRepository.save(restaurantAdmin);
    }

    public void delete(final Long id) {
        restaurantAdminRepository.deleteById(id);
    }

    /**
     * Maps a restaurant admin entity to a DTO.
     * @param restaurantAdmin The entity.
     * @param restaurantAdminDTO The resulting DTO.
     * @return The resulting DTO.
     */

    private RestaurantAdminDTO mapToDTO(final RestaurantAdmin restaurantAdmin,
            final RestaurantAdminDTO restaurantAdminDTO) {
        log.info("Mapping restaurant admin to DTO");
        restaurantAdminDTO.setUser(restaurantAdmin.getUser() == null ? null : restaurantAdmin.getUser().getId());
        restaurantAdminDTO.setRestaurant(restaurantAdmin.getRestaurant() == null ? null : restaurantAdmin.getRestaurant().getId());
        return restaurantAdminDTO;
    }

    /**
     * Maps a restaurant admin DTO to an entity.
     * @param restaurantAdminDTO The DTO.
     * @param restaurantAdmin The resulting entity.
     * @return The resulting entity.
     */

    private RestaurantAdmin mapToEntity(final RestaurantAdminDTO restaurantAdminDTO, final RestaurantAdmin restaurantAdmin) {
        log.info("Mapping restaurant admin DTO to entity");
        if (restaurantAdminDTO.getUser() != null && (restaurantAdmin.getUser() == null
                || !restaurantAdmin.getUser().getId().equals(restaurantAdminDTO.getUser()))) {
            final User user = userRepository.findById(restaurantAdminDTO.getUser())
                    .orElseThrow(() -> {
                        log.warn("User with ID {} not found", restaurantAdminDTO.getUser());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                    });
            restaurantAdmin.setUser(user);
        }
        return restaurantAdmin;
    }

}
