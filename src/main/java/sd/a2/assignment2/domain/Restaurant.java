package sd.a2.assignment2.domain;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Restaurant {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String zones;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id", nullable = false)
    private RestaurantAdmin admin;

    @OneToMany(mappedBy = "restaurant")
    private List<Food> restaurantFoods;

    @OneToMany(mappedBy = "restaurant")
    private List<Order> restaurantOrders;

}
