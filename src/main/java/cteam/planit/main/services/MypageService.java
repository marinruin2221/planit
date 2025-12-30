package cteam.planit.main.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.MypageDTO;

@Service
public class MypageService
{
	@Autowired
	public UsersRepository usersRepository;

	public MypageDTO information(MypageDTO mypageDTO) throws Exception
	{
		MypageDTO data = new MypageDTO();
		Optional<UsersDAO> info = usersRepository.findByUserId(mypageDTO.getUserId());

		if(info.isPresent())
		{
			data.setUserId(info.get().getUserId());
			data.setUserPw(info.get().getUserPw());
			data.setName(info.get().getName());
			data.setEmail(info.get().getEmail());
			data.setBirthY(info.get().getBirthY());
			data.setBirthM(info.get().getBirthM());
			data.setBirthD(info.get().getBirthD());
			data.setGender(info.get().getGender());
		}

		return data;
	}
}