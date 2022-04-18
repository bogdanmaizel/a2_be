package sd.a2.assignment2.rest;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import sd.a2.assignment2.model.FoodDTO;
import sd.a2.assignment2.service.FoodService;


@RestController
@RequestMapping(value = "/api/foods", produces = MediaType.APPLICATION_JSON_VALUE)
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(final FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping
    public ResponseEntity<List<FoodDTO>> getAllFoods() {
        return ResponseEntity.ok(foodService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFood(@PathVariable final Long id) {
        return ResponseEntity.ok(foodService.get(id));
    }

    @GetMapping("/menu-{id}")
    public ResponseEntity<?> getMenu(@PathVariable final Long id) {
        return ResponseEntity.ok(foodService.getMenu(id));
    }

    @PostMapping
    public ResponseEntity<?> createFood(@RequestBody @Valid final FoodDTO foodDTO) {
        return new ResponseEntity<>(foodService.create(foodDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFood(@PathVariable final Long id,
            @RequestBody @Valid final FoodDTO foodDTO) {
        foodService.update(id, foodDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable final Long id) {
        foodService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
