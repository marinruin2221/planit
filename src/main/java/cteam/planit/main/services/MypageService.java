package cteam.planit.main.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.BreakdownDAO;
import cteam.planit.main.dao.BreakdownRepository;
import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.BreakdownDTO;
import cteam.planit.main.dto.InformationDTO;

@Service
public class MypageService
{
	@Autowired
	public UsersRepository usersRepository;

	@Autowired
	public BreakdownRepository breakdownRepository;

	public InformationDTO information(InformationDTO informationDTO) throws Exception
	{
		InformationDTO data = new InformationDTO();
		Optional<UsersDAO> info = usersRepository.findById(informationDTO.getId());

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

	public Page<BreakdownDAO> breakdown(BreakdownDTO breakdownDTO) throws Exception
	{
		PageRequest pageable = PageRequest.of(breakdownDTO.getPage(), breakdownDTO.getSize());
		
		return breakdownRepository.findByUsersIdAndDeleteYNAndNameContaining
		(
			breakdownDTO.getUsersId(),
			"N",
			breakdownDTO.getWord(),
			pageable
		);
	}

	public void breakdownCancel(BreakdownDTO breakdownDTO) throws Exception
	{
		Optional<BreakdownDAO> breakdown = breakdownRepository.findById(breakdownDTO.getId());

		if(breakdown.isPresent())
		{
			BreakdownDAO data = breakdown.get();
			data.setStatus("2");

			breakdownRepository.save(data);
		}
	}
}