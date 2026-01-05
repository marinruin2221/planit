package cteam.planit.main.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
