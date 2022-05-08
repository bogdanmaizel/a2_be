package sd.a2.assignment2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Customer;
import sd.a2.assignment2.domain.RestaurantAdmin;
import sd.a2.assignment2.domain.User;
import sd.a2.assignment2.model.CustomerDTO;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.model.UserDTO;
import sd.a2.assignment2.repos.CustomerRepository;
import sd.a2.assignment2.repos.RestaurantAdminRepository;
import sd.a2.assignment2.repos.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles various logic and operations concerning the authentication methods.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantAdminRepository restaurantAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User {} not found for TOKEN", username);
                    return new UsernameNotFoundException("User not found for TOKEN");
                });
        log.info("User {} found", username);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.getAdmin() != null) authorities.add(new SimpleGrantedAuthority("ADMIN"));
        if (user.getCustomer() != null) authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), authorities);
    }

    /**
     * Handle admin user login.
     * @param userDTO User (admin) data.
     * @return restaurant admin DTO.
     */

    public RestaurantAdminDTO checkCredentialsAdmin(final UserDTO userDTO) {
        log.info("Checking {}'s admin credentials", userDTO.getUsername());
        final User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> {
                    log.warn("User {} not found", userDTO.getUsername());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });
        final RestaurantAdminDTO restaurantAdminDTO = new RestaurantAdminDTO();
        if (user.getAdmin() == null) {
            log.warn("User not admin");
            throw new RuntimeException("User not admin");
        }
        if (BCrypt.checkpw(userDTO.getPassword(), user.getPassword()) ||
                passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            restaurantAdminDTO.setUser(user.getAdmin().getId());
            restaurantAdminDTO.setRestaurant(
                    user.getAdmin().getRestaurant() == null ? null : user.getAdmin().getRestaurant().getId());
            return restaurantAdminDTO;
        }
        log.warn("Incorrect credentials");
            return null;
    }

    /**
     * Handle customer user login.
     * @param userDTO User (customer) data.
     * @return Customer DTO.
     */

    public CustomerDTO checkCredentialsCustomer(final UserDTO userDTO) {
        log.info("Checking {}'s customer credentials", userDTO.getUsername());
        final User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> {
                    log.warn("User {} not found", userDTO.getUsername());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });
        final CustomerDTO customerDTO = new CustomerDTO();
            if (user.getCustomer() == null) {
                log.warn("User not customer");
                throw new RuntimeException("User not customer");
            }
            if (BCrypt.checkpw(userDTO.getPassword(), user.getPassword()) ||
                    passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                customerDTO.setCustomer(user.getCustomer().getId());
                return customerDTO;
            }
        log.warn("Incorrect credentials");
        return null;
    }

    /**
     * Creates an admin user directly.
     * @param userDTO User details.
     * @return The admin ID.
     */

    public Long createAdmin(final UserDTO userDTO) {
        log.info("Creating admin account with username {}", userDTO.getUsername());
        final User user = new User();
        mapToEntity(userDTO, user);
        final RestaurantAdmin restaurantAdmin = new RestaurantAdmin();
        restaurantAdmin.setUser(user);
        userRepository.save(user);
        return restaurantAdminRepository.save(restaurantAdmin).getId();
    }

    /**
     * Creates a customer user directly.
     * @param userDTO User details.
     * @return The customer ID.
     */

    public Long createCustomer(final UserDTO userDTO) {
        log.info("Creating customer account with username {}", userDTO.getUsername());
        final User user = new User();
        mapToEntity(userDTO, user);
        final Customer customer = new Customer();
        customer.setUser(user);
        userRepository.save(user);
        return customerRepository.save(customer).getId();
    }

    /**
     * Maps user entity to DTO.
     * @param user The entity.
     * @param userDTO The resulting DTO.
     * @return The resulting DTO.
     */

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        log.info("Mapping user entity {} to DTO", user.getUsername());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    /**
     * Maps a user DTO to entity.
     * @param userDTO The DTO.
     * @param user The resulting entity.
     * @return The resulting entity.
     */

    private User mapToEntity(final UserDTO userDTO, final User user) {
        log.info("Mapping user DTO {} to entity", userDTO.getUsername());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return user;
    }


}
