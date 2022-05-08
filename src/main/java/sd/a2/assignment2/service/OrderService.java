package sd.a2.assignment2.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sd.a2.assignment2.domain.Customer;
import sd.a2.assignment2.domain.Food;
import sd.a2.assignment2.domain.Order;
import sd.a2.assignment2.domain.Restaurant;
import sd.a2.assignment2.model.OrderDTO;
import sd.a2.assignment2.model.OrderStatus;
import sd.a2.assignment2.repos.CustomerRepository;
import sd.a2.assignment2.repos.FoodRepository;
import sd.a2.assignment2.repos.OrderRepository;
import sd.a2.assignment2.repos.RestaurantRepository;

/**
 * Handles various logic and operations concerning the orders.
 */

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    /**
     * @param id The customer ID.
     * @return A list of all orders made by the specified customer.
     */

    public List<OrderDTO> findFromCustomer(Long id) {
        log.info("Fetching all orders from customer {}", id);
        return orderRepository.findByCustomerId(id)
                .stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .collect(Collectors.toList());
    }

    /**
     * @param id The restaurant ID.
     * @return A list of all orders sent to the specified restaurant.
     */

    public List<OrderDTO> findFromRestaurant(Long id) {
        log.info("Fetching all orders for restaurant {}", id);
        return orderRepository.findByRestaurantId(id)
                .stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new order
     * @param orderDTO The order data.
     * @return The new order's ID.
     */

    public Long create(final OrderDTO orderDTO) {
        log.info("Creating new order from customer {} to restaurant {}",
                orderDTO.getCustomer(),
                orderDTO.getRestaurant());
        final Order order = new Order();
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getId();
    }

    /**
     * Updates the order status using State DP
     * @param id The selected order.
     * @param advance When the order is in PENDING state, checks whether to accept or decline the order, changing its state to ACCEPTED or DECLINED respectively
     */

    public void update(final Long id, final int advance) { //State DP
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });
        log.info("Changing order {} status (current -> {})", id, order.getStatus().toString());
        switch (order.getStatus()) {
            case PENDING -> {
                if (advance != 0) order.setStatus(OrderStatus.ACCEPTED);
                else order.setStatus(OrderStatus.DECLINED);
            }
            case ACCEPTED -> {
                if (advance != 0) order.setStatus(OrderStatus.IN_DELIVERY);
            }
            case IN_DELIVERY -> {
                if (advance != 0) order.setStatus(OrderStatus.DELIVERED);
            }
        }
        log.info("Order {} new status -> {}", id, order.getStatus().toString());
        orderRepository.save(order);
    }

    /**
     * Maps an order object to a DTO.
     * @param order Order to be mapped
     * @param orderDTO Resulting DTO
     * @return Resulting DTO.
     */

    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
        log.info("Creating DTO from order entity {}", order.getId());
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getId());
        orderDTO.setRestaurant(order.getRestaurant() == null ? null : order.getRestaurant().getId());
        orderDTO.setOrderFoods(order.getOrderFoodFoods() == null ? null : order.getOrderFoodFoods().stream()
                .map(Food::getId)
                .collect(Collectors.toList()));
        return orderDTO;
    }

    /**
     * Maps an order DTO to an entity.
     * @param orderDTO The DTO to be mapped.
     * @param order The resulting order.
     * @return The resulting order.
     */

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        log.info("Creating entity from order DTO");
        if (orderDTO.getCustomer() != null && (order.getCustomer() == null || !order.getCustomer().getId().equals(orderDTO.getCustomer()))) {
            final Customer customer = customerRepository.findById(orderDTO.getCustomer())
                    .orElseThrow(() -> {
                        log.warn("Customer with ID {} not found", orderDTO.getCustomer());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
                    });
            order.setCustomer(customer);
        }
        if (orderDTO.getRestaurant() != null && (order.getRestaurant() == null || !order.getRestaurant().getId().equals(orderDTO.getRestaurant()))) {
            final Restaurant restaurant = restaurantRepository.findById(orderDTO.getRestaurant())
                    .orElseThrow(() -> {
                        log.warn("Restaurant with ID {} not found", orderDTO.getRestaurant());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found");
                    });
            order.setRestaurant(restaurant);
        }
        if (orderDTO.getOrderFoods() != null) {
            final List<Food> orderFoods = foodRepository.findAllById(orderDTO.getOrderFoods());
            if (orderFoods.size() != orderDTO.getOrderFoods().size()) {
                log.warn("One or more of the foods from the order were not found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of orderFoods not found");
            }
            order.setOrderFoodFoods(orderFoods.stream().collect(Collectors.toList()));
        }
        return order;
    }

}
