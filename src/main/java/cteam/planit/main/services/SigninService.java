package cteam.planit.main.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.SigninDTO;
import cteam.planit.main.utils.JWTUtil;

@Service
public class SigninService
{
	@Autowired
	public UsersRepository usersRepository;

	@Autowired
	public JWTUtil jwtUtil;

	public SigninDTO signin(SigninDTO signinDTO) throws Exception
	{
		SigninDTO data = new SigninDTO();
		Optional<UsersDAO> user = usersRepository.findByUserIdAndUserPw(signinDTO.getUserId(), signinDTO.getUserPw());

		if(user.isPresent())
		{
			if(user.get().getDeleteYN().equals("Y"))
			{
				data.setResult("W");
			}
			else
			{
				String token = jwtUtil.createToken(user.get().getUserId());
	
				data.setId(user.get().getId());
				data.setUserId(user.get().getUserId());
				data.setUserPw(user.get().getUserPw());
				data.setToken(token);
				data.setResult("Y");
			}
		}
		else
		{
			data.setResult("N");
		}

		return data;
	}
}