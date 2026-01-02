package cteam.planit.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponseDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
}
