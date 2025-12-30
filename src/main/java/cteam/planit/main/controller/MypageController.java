package cteam.planit.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dto.MypageDTO;
import cteam.planit.main.services.MypageService;

@RestController
@RequestMapping("/api/mypage")
public class MypageController
{
	@Autowired
	public MypageService service;

	@PostMapping("/information")
	public MypageDTO information(@RequestBody MypageDTO mypageDTO) throws Exception
	{
		return service.information(mypageDTO);
	}
}