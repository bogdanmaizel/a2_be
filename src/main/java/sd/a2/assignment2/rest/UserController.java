package sd.a2.assignment2.rest;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.a2.assignment2.model.CustomerDTO;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.model.UserDTO;
import sd.a2.assignment2.service.UserService;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/login-admin")
    public @ResponseBody ResponseEntity<?> loginAdmin(@RequestBody @Valid final UserDTO userDTO) {
        log.info("API access - Attempting admin login for {}", userDTO.getUsername());
        RestaurantAdminDTO login = userService.checkCredentialsAdmin(userDTO);
        if (login != null) {
            log.info("API access - Login for {} successful", userDTO.getUsername());
            return ResponseEntity.ok(login);
        }
        else {
            log.warn("API access - Login for {} failed", userDTO.getUsername());
            return new ResponseEntity<>(ResponseEntity.noContent(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login-customer")
    public @ResponseBody ResponseEntity<?> loginCustomer(@RequestBody @Valid final UserDTO userDTO) {
        log.info("API access - Attempting customer login for {}", userDTO.getUsername());
        CustomerDTO login = userService.checkCredentialsCustomer(userDTO);
        if (login !=null) {
            log.info("API access - Login for {} successful", userDTO.getUsername());
            return ResponseEntity.ok(login);
        }
        else {
            log.warn("API access - Login for {} failed", userDTO.getUsername());
            return new ResponseEntity<>(ResponseEntity.noContent(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/new-admin")
    public ResponseEntity<?> createAdminUser(@RequestBody @Valid final UserDTO userDTO) {
        log.info("API access - Creating new admin user for {}", userDTO.getUsername());
        Long register = userService.createAdmin(userDTO);
        return new ResponseEntity<>(new RestaurantAdminDTO(register), HttpStatus.CREATED);
    }

    @PostMapping("/new-customer")
    public @ResponseBody ResponseEntity<?> createCustomerUser(@RequestBody @Valid final UserDTO userDTO) {
        log.info("API access - Creating new customer user for {}", userDTO.getUsername());
        Long register = userService.createCustomer(userDTO);
        return new ResponseEntity<>(new CustomerDTO(register), HttpStatus.CREATED);
    }

}
