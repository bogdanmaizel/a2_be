package sd.a2.assignment2.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
public class UserDTO {

    @NotNull
    @Size(max = 30)
    private String username;

    @NotNull
    private String password;

}
