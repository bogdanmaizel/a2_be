package sd.a2.assignment2.rest;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable final Long id) {
        return ResponseEntity.ok(orderService.get(id));
    }

    @GetMapping("/from-user/{id}")
    public ResponseEntity<?> getOrdersFromCustomer(@PathVariable final Long id) {
        return ResponseEntity.ok(orderService.findFromCustomer(id));
    }

    @GetMapping("/from-restaurant/{id}")
    public ResponseEntity<?> getOrdersFromRestaurant(@PathVariable final Long id) {
        return ResponseEntity.ok(orderService.findFromAdmin(id));
    }

    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody @Valid final OrderDTO orderDTO) {
        return new ResponseEntity<>(orderService.create(orderDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/{advanceOrCancel}")
    public ResponseEntity<Void> updateOrder(@PathVariable final Long id, @PathVariable int advanceOrCancel) {
        orderService.update(id, advanceOrCancel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable final Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
