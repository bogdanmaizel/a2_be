package sd.a2.assignment2.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RestaurantDTO {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String name;

    @NotNull
    @Size(max = 255)
    private String location;

    @NotNull
    @Size(max = 255)
    private String zones;

    @NotNull
    private Long admin;

}
