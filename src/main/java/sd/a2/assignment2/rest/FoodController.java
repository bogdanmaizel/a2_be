package sd.a2.assignment2.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sd.a2.assignment2.model.FoodDTO;
import sd.a2.assignment2.service.FoodService;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/api/foods", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class FoodController {
    private final FoodService foodService;

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

}
