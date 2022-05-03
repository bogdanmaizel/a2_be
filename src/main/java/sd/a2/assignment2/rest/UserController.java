package sd.a2.assignment2.rest;

import java.util.List;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.a2.assignment2.model.CustomerDTO;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.model.UserDTO;
import sd.a2.assignment2.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login-admin")
    public @ResponseBody ResponseEntity<?> loginAdmin(@RequestBody @Valid final UserDTO userDTO) {
        RestaurantAdminDTO login = userService.checkCredentialsAdmin(userDTO);
        if (login != null)
            return ResponseEntity.ok(login);
        else
            return new ResponseEntity<>(ResponseEntity.noContent(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/login-customer")
    public @ResponseBody ResponseEntity<?> loginCustomer(@RequestBody @Valid final UserDTO userDTO) {
        CustomerDTO login = userService.checkCredentialsCustomer(userDTO);
        if (login !=null)
            return new ResponseEntity<>(login, HttpStatus.OK);//ResponseEntity.ok(login);
        else
            return new ResponseEntity<>(ResponseEntity.noContent(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/new-admin")
    public ResponseEntity<?> createAdminUser(@RequestBody @Valid final UserDTO userDTO) {
        Long register = userService.createAdmin(userDTO);
        return new ResponseEntity<>(new RestaurantAdminDTO(register), HttpStatus.CREATED);
    }

    @PostMapping("/new-customer")
    public @ResponseBody ResponseEntity<?> createCustomerUser(@RequestBody @Valid final UserDTO userDTO) {
        Long register = userService.createCustomer(userDTO);
        return new ResponseEntity<>(new CustomerDTO(register), HttpStatus.CREATED);
    }

}
