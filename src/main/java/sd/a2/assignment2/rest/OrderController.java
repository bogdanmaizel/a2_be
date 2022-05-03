package sd.a2.assignment2.rest;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import sd.a2.assignment2.model.OrderDTO;
import sd.a2.assignment2.service.OrderService;


@RestController
@RequestMapping(value = "/api/orders", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/from-user/{id}")
    public ResponseEntity<?> getOrdersFromCustomer(@PathVariable final Long id) {
        log.info("API access - Fetching orders for customer, ID {}", id);
        return ResponseEntity.ok(orderService.findFromCustomer(id));
    }

    @GetMapping("/from-restaurant/{id}")
    public ResponseEntity<?> getOrdersFromRestaurant(@PathVariable final Long id) {
        log.info("API access - Fetching orders for restaurant, ID {}", id);
        return ResponseEntity.ok(orderService.findFromRestaurant(id));
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody @Valid final OrderDTO orderDTO) {
        log.info("API access - Creating order from customer {} to restaurant {}", orderDTO.getCustomer(), orderDTO.getRestaurant());
        return new ResponseEntity<>(orderService.create(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/{advanceOrCancel}")
    public ResponseEntity<Void> updateOrder(@PathVariable final Long id, @PathVariable int advanceOrCancel) {
        log.info("API access - Updating order status, ID {}", id);
        orderService.update(id, advanceOrCancel);
        return ResponseEntity.ok().build();
    }

}
