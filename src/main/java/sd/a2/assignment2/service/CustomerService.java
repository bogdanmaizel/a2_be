package sd.a2.assignment2.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Customer;
import sd.a2.assignment2.domain.User;
import sd.a2.assignment2.model.CustomerDTO;
import sd.a2.assignment2.repos.CustomerRepository;
import sd.a2.assignment2.repos.UserRepository;

/**
 * Handles various logic and operations concerning the customers.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public List<CustomerDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Returns customer entity based on ID.
     * @param id The customer ID.
     * @return A DTO with the customer information.
     */

    public CustomerDTO get(final Long id) {
        log.info("Fetching customer {}", id);
        return customerRepository.findById(id)
                .map(customer -> mapToDTO(customer, new CustomerDTO()))
                .orElseThrow(() -> {
                    log.warn("Customer {} not found, no changes made", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
                });
    }

    /**
     * Create a customer entity.
     * @param customerDTO Customer data.
     * @return Customer ID.
     */

    public Long create(final CustomerDTO customerDTO) {
        final Customer customer = new Customer();
        mapToEntity(customerDTO, customer);
        log.info("Created customer {}", customerDTO.getCustomer());
        return customerRepository.save(customer).getId();
    }

    public void update(final Long id, final CustomerDTO customerDTO) {
        final Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer {} not found, no changes made", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
                });
        mapToEntity(customerDTO, customer);
        customerRepository.save(customer);
    }

    public void delete(final Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Maps entities to DTOs.
     * @param customer The entity.
     * @param customerDTO The DTO.
     * @return Customer DTO.
     */

    private CustomerDTO mapToDTO(final Customer customer, final CustomerDTO customerDTO) {
        log.info("Create DTO from entity {}", customer.getId());
        customerDTO.setCustomer(customer.getUser() == null ? null : customer.getUser().getId());
        return customerDTO;
    }

    /**
     * Maps DTOs to entities.
     * @param customerDTO The DTO.
     * @param customer The entity.
     * @return Customer entity.
     */

    private Customer mapToEntity(final CustomerDTO customerDTO, final Customer customer) {
        log.info("Create entity from DTO {}", customerDTO.getCustomer());
        if (customerDTO.getCustomer() != null && (customer.getUser() == null || !customer.getUser().getId().equals(customerDTO.getCustomer()))) {
            final User user = userRepository.findById(customerDTO.getCustomer())
                    .orElseThrow(() -> {
                        log.warn("Customer {} not found, no changes made", customerDTO.getCustomer());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
                    });
            customer.setUser(user);
        }
        return customer;
    }

}
