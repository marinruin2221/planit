package cteam.planit.main.dto;

import lombok.Data;

@Data
public class SigninDTO
{
	private Long id;		// 사용자 시퀀스
	private String userId;	// 사용자 ID
	private String userPw;	// 사용자 PW
	private String email;	// 사용자 이메일
	private String birthY;	// 사용자 생년
	private String birthM;	// 사용자 생월
	private String birthD;	// 사용자 생일
	private String token;	// 사용자 토큰
	private String result;	// 결과 (Y:로그인 성공, N:로그인 실패, W:회원탈퇴)
}