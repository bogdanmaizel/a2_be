package sd.a2.assignment2.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderDTO {

    private Long id;

    @NotNull
    private Long customer;

    @NotNull
    private Long restaurant;

    private OrderStatus status;

    private List<Long> orderFoods;

}
