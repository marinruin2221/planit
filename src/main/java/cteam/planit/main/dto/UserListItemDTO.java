package cteam.planit.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserListItemDTO {
    private Long id;
    private String userId;
    private String name;
    private String userPw;
    private String email;
    private String birthY;
    private String birthM;
    private String birthD;
    private String gender;
    private String deleteYN;
}
