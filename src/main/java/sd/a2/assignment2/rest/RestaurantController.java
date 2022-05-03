package sd.a2.assignment2.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.a2.assignment2.model.RestaurantDTO;
import sd.a2.assignment2.service.RestaurantService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/api/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        log.info("API access - Fetching restaurants list");
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/admin-{adminId}")
    public ResponseEntity<?> getRestaurantByAdminId(@PathVariable final Long adminId) {
        log.info("API access - Fetching restaurant for admin, ID {}", adminId);
        return ResponseEntity.ok(restaurantService.getFromAdmin(adminId));
    }

    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody @Valid final RestaurantDTO restaurantDTO) {
        log.info("API access - Creating restaurant {} for admin, ID {}", restaurantDTO.getName(), restaurantDTO.getAdmin());
        return new ResponseEntity<>(restaurantService.create(restaurantDTO), HttpStatus.CREATED);
    }

}
