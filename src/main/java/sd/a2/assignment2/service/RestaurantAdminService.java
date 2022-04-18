package sd.a2.assignment2.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.RestaurantAdmin;
import sd.a2.assignment2.domain.User;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.repos.RestaurantAdminRepository;
import sd.a2.assignment2.repos.UserRepository;


@Service
public class RestaurantAdminService {

    private final RestaurantAdminRepository restaurantAdminRepository;
    private final UserRepository userRepository;

    public RestaurantAdminService(final RestaurantAdminRepository restaurantAdminRepository,
            final UserRepository userRepository) {
        this.restaurantAdminRepository = restaurantAdminRepository;
        this.userRepository = userRepository;
    }

    public List<RestaurantAdminDTO> findAll() {
        return restaurantAdminRepository.findAll()
                .stream()
                .map(restaurantAdmin -> mapToDTO(restaurantAdmin, new RestaurantAdminDTO()))
                .collect(Collectors.toList());
    }

    public RestaurantAdminDTO get(final Long id) {
        return restaurantAdminRepository.findById(id)
                .map(restaurantAdmin -> mapToDTO(restaurantAdmin, new RestaurantAdminDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final RestaurantAdminDTO restaurantAdminDTO) {
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

    private RestaurantAdminDTO mapToDTO(final RestaurantAdmin restaurantAdmin,
            final RestaurantAdminDTO restaurantAdminDTO) {
        restaurantAdminDTO.setUser(restaurantAdmin.getUser() == null ? null : restaurantAdmin.getUser().getId());
        restaurantAdminDTO.setRestaurant(restaurantAdmin.getRestaurant() == null ? null : restaurantAdmin.getRestaurant().getId());
        return restaurantAdminDTO;
    }

    private RestaurantAdmin mapToEntity(final RestaurantAdminDTO restaurantAdminDTO, final RestaurantAdmin restaurantAdmin) {
        if (restaurantAdminDTO.getUser() != null && (restaurantAdmin.getUser() == null
                || !restaurantAdmin.getUser().getId().equals(restaurantAdminDTO.getUser()))) {
            final User user = userRepository.findById(restaurantAdminDTO.getUser())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
            restaurantAdmin.setUser(user);
        }
        return restaurantAdmin;
    }

}
