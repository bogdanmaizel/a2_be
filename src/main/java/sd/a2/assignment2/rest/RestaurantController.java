package sd.a2.assignment2.rest;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sd.a2.assignment2.model.RestaurantDTO;
import sd.a2.assignment2.service.RestaurantService;


@RestController
@RequestMapping(value = "/api/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/admin-{adminId}")
    public ResponseEntity<?> getRestaurantByAdminId(@PathVariable final Long adminId) {
        return ResponseEntity.ok(restaurantService.getFromAdmin(adminId));
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(
            @RequestBody @Valid final RestaurantDTO restaurantDTO) {
        return new ResponseEntity<>(restaurantService.create(restaurantDTO), HttpStatus.CREATED);
    }

}
