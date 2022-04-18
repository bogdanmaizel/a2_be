package sd.a2.assignment2.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.a2.assignment2.model.RestaurantAdminDTO;
import sd.a2.assignment2.service.RestaurantAdminService;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/restaurantAdmins", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantAdminController {

    private final RestaurantAdminService restaurantAdminService;

    public RestaurantAdminController(final RestaurantAdminService restaurantAdminService) {
        this.restaurantAdminService = restaurantAdminService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantAdminDTO> getRestaurantAdmin(@PathVariable final Long id) {
        return ResponseEntity.ok(restaurantAdminService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createRestaurantAdmin(
            @RequestBody @Valid final RestaurantAdminDTO restaurantAdminDTO) { //SIGN UP part 2: assign restaurantAdmin to user
        return new ResponseEntity<>(restaurantAdminService.create(restaurantAdminDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRestaurantAdmin(@PathVariable final Long id,
            @RequestBody @Valid final RestaurantAdminDTO restaurantAdminDTO) {
        restaurantAdminService.update(id, restaurantAdminDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurantAdmin(@PathVariable final Long id) {
        restaurantAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
