package cteam.planit.main.dto;

import lombok.Data;

@Data
public class MypageDTO
{
	private String userId;
	private String userPw;
	private String name;
	private String email;
	private String birthY;
	private String birthM;
	private String birthD;
	private String gender;
}