package sd.a2.assignment2.model;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class RestaurantAdminDTO {

    @NotNull
    private Long user;

    private Long restaurant;

    public RestaurantAdminDTO(Long user) {
        this.user = user;
        this.restaurant=null;
    }
}
