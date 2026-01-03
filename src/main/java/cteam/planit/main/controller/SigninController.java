package cteam.planit.main.controller;

import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dto.SigninDTO;
import cteam.planit.main.services.SigninService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/signin")
public class SigninController
{
	@Autowired
	public SigninService service;

	@PostMapping("/signin")
	public SigninDTO signin(@RequestBody SigninDTO signinDTO) throws Exception
	{
		return service.signin(signinDTO);
	}

	@PostMapping("/findid")
	public SigninDTO findid(@RequestBody SigninDTO signinDTO) throws Exception
	{
		return service.findid(signinDTO);
	}
}