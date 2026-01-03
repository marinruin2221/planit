package cteam.planit.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dao.BreakdownDAO;
import cteam.planit.main.dto.BreakdownDTO;
import cteam.planit.main.dto.InformationDTO;
import cteam.planit.main.dto.ReviewDTO;
import cteam.planit.main.entity.Review;
import cteam.planit.main.services.MypageService;

@RestController
@RequestMapping("/api/mypage")
public class MypageController
{
	@Autowired
	public MypageService service;

	@PostMapping("/information")
	public InformationDTO information(@RequestBody InformationDTO informationDTO) throws Exception
	{
		return service.information(informationDTO);
	}

	@PostMapping("/informationUpdate")
	public void informationUpdate(@RequestBody InformationDTO informationDTO) throws Exception
	{
		service.informationUpdate(informationDTO);
	}

	@PostMapping("/breakdown")
	public Page<BreakdownDAO> breakdown(@RequestBody BreakdownDTO breakdownDTO) throws Exception
	{
		return service.breakdown(breakdownDTO);
	}

	@PostMapping("/breakdownCancel")
	public void breakdownCancel(@RequestBody BreakdownDTO breakdownDTO) throws Exception
	{
		service.breakdownCancel(breakdownDTO);
	}

	@PostMapping("/review")
	public Page<Review> review(@RequestBody ReviewDTO reviewDTO) throws Exception
	{
		return service.review(reviewDTO);
	}

	@PostMapping("/reviewDelete")
	public void reviewDelete(@RequestBody ReviewDTO reviewDTO) throws Exception
	{
		service.reviewDelete(reviewDTO);
	}

	@PostMapping("/withdraw")
	public void withdraw(@RequestBody InformationDTO informationDTO) throws Exception
	{
		service.withdraw(informationDTO);
	}
}