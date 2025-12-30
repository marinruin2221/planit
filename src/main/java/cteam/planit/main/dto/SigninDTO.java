package cteam.planit.main.dto;

import lombok.Data;

@Data
public class SigninDTO
{
	private String userId;
	private String userPw;
	private String token;
	private String result;
}