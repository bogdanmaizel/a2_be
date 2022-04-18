package sd.a2.assignment2.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantAdminRepository restaurantAdminRepository;

    public UserService(final UserRepository userRepository, CustomerRepository customerRepository, RestaurantAdminRepository restaurantAdminRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.restaurantAdminRepository = restaurantAdminRepository;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .collect(Collectors.toList());
    }

    public RestaurantAdminDTO checkCredentialsAdmin(final UserDTO userDTO) {
        final User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final RestaurantAdminDTO restaurantAdminDTO = new RestaurantAdminDTO();
        if (user.getAdmin() == null)
            throw new RuntimeException("User not admin");
        if (BCrypt.checkpw(userDTO.getPassword(), user.getPassword())) {
            restaurantAdminDTO.setUser(user.getAdmin().getId());
            restaurantAdminDTO.setRestaurant(
                    user.getAdmin().getRestaurant() == null ? null : user.getAdmin().getRestaurant().getId());
            return restaurantAdminDTO;
        }
            return null;
    }

    public CustomerDTO checkCredentialsCustomer(final UserDTO userDTO) {
        //System.out.println(userDTO.getUsername() + "\t" + userDTO.getPassword());
        final User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final CustomerDTO customerDTO = new CustomerDTO();
            if (user.getCustomer() == null)
                throw new RuntimeException("User not customer");
            if (BCrypt.checkpw(userDTO.getPassword(), user.getPassword())) {
                customerDTO.setCustomer(user.getCustomer().getId());
            }
        return customerDTO;
    }

    public UserDTO get(final Long id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public Long createAdmin(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        final RestaurantAdmin restaurantAdmin = new RestaurantAdmin();
        restaurantAdmin.setUser(user);
        userRepository.save(user);
        return restaurantAdminRepository.save(restaurantAdmin).getId();
    }

    public Long createCustomer(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        final Customer customer = new Customer();
        customer.setUser(user);
        userRepository.save(user);
        return customerRepository.save(customer).getId();
    }

    public void update(final Long id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt()));
        return user;
    }

}
