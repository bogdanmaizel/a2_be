package sd.a2.assignment2.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
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


@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodRepository foodRepository;

    public OrderService(final OrderRepository orderRepository,
            final CustomerRepository customerRepository,
            final RestaurantRepository restaurantRepository, final FoodRepository foodRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodRepository = foodRepository;
    }

    public List<OrderDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> findFromCustomer(Long id) {
        return orderRepository.findByCustomerId(id)
                .stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .collect(Collectors.toList());
    }

    public List<OrderDTO> findFromAdmin(Long id) {
        return orderRepository.findByRestaurantId(id)
                .stream()
                .map(order -> mapToDTO(order, new OrderDTO()))
                .collect(Collectors.toList());
    }

    public OrderDTO get(final Long id) {
        return orderRepository.findById(id)
                .map(order -> mapToDTO(order, new OrderDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Long create(final OrderDTO orderDTO) {
        final Order order = new Order();
        mapToEntity(orderDTO, order);
        return orderRepository.save(order).getId();
    }

    public void update(final Long id, final int advance) { //State DP
        final Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        switch (order.getStatus()) {
            case PENDING -> {
                if (advance != 0) order.setStatus(OrderStatus.ACCEPTED);
                else order.setStatus(OrderStatus.DECLINED);
            }
            case ACCEPTED -> order.setStatus(OrderStatus.IN_DELIVERY);
            case IN_DELIVERY -> order.setStatus(OrderStatus.DELIVERED);
        }
        orderRepository.save(order);
    }

    public void delete(final Long id) {
        orderRepository.deleteById(id);
    }

    private OrderDTO mapToDTO(final Order order, final OrderDTO orderDTO) {
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setCustomer(order.getCustomer() == null ? null : order.getCustomer().getId());
        orderDTO.setRestaurant(order.getRestaurant() == null ? null : order.getRestaurant().getId());
        orderDTO.setOrderFoods(order.getOrderFoodFoods() == null ? null : order.getOrderFoodFoods().stream()
                .map(Food::getId)
                .collect(Collectors.toList()));
        return orderDTO;
    }

    private Order mapToEntity(final OrderDTO orderDTO, final Order order) {
        if (orderDTO.getCustomer() != null && (order.getCustomer() == null || !order.getCustomer().getId().equals(orderDTO.getCustomer()))) {
            final Customer customer = customerRepository.findById(orderDTO.getCustomer())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found"));
            order.setCustomer(customer);
        }
        if (orderDTO.getRestaurant() != null && (order.getRestaurant() == null || !order.getRestaurant().getId().equals(orderDTO.getRestaurant()))) {
            final Restaurant restaurant = restaurantRepository.findById(orderDTO.getRestaurant())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "restaurant not found"));
            order.setRestaurant(restaurant);
        }
        if (orderDTO.getOrderFoods() != null) {
            final List<Food> orderFoods = foodRepository.findAllById(orderDTO.getOrderFoods());
            if (orderFoods.size() != orderDTO.getOrderFoods().size()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "one of orderFoods not found");
            }
            order.setOrderFoodFoods(orderFoods.stream().collect(Collectors.toList()));
        }
        return order;
    }

}
