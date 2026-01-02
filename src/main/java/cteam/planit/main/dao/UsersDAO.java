package cteam.planit.main.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USERS")
@SequenceGenerator
(
	name = "users_seq_gen",
	sequenceName = "USERS_SEQ",
	allocationSize = 1
)
@Data
public class UsersDAO
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
	private Long id;			// 사용자 시퀀스
	private String userId;		// 사용자 ID
	private String userPw;		// 사용자 PW
	private String name;		// 사용자 이름
	private String email;		// 사용자 이메일
	private String birthY;		// 사용자 생년
	private String birthM;		// 사용자 생월
	private String birthD;		// 사용자 생일
	private String gender;		// 사용자 성별 (M:남자, F:여자)
	private String deleteYN;	// 사용자 삭제 여부 (회원탈퇴) (Y:삭제됨, N:삭제안됨)
}