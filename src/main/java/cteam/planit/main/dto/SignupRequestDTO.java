package cteam.planit.main.dto;

import java.util.Map;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequestDTO {

    // 프론트: form.username
    @NotBlank(message = "아이디는 필수입니다.")
    private String username;

    // 프론트: form.nickname
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    // 프론트: form.password
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    // 프론트: form.email
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    // 프론트: form.birth {year,month,day}
    @NotNull(message = "생년월일은 필수입니다.")
    private BirthDTO birth;

    // 프론트: form.gender ("male"|"female")
    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    // 프론트: form.terms {agreement:true, privacyconsent:true, age14:true, ...}
    @NotNull(message = "약관 동의 정보가 필요합니다.")
    private Map<String, Boolean> terms;

    @Data
    public static class BirthDTO {
        @NotBlank(message = "출생년도는 필수입니다.")
        private String year;

        @NotBlank(message = "출생월은 필수입니다.")
        private String month;

        @NotBlank(message = "출생일은 필수입니다.")
        private String day;
    }
}
