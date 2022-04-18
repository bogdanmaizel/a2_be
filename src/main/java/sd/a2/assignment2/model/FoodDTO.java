package sd.a2.assignment2.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FoodDTO {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String name;

    @NotNull
    @Size(max = 255)
    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Category category;

    @NotNull
    private Long restaurant;

}
