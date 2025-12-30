package cteam.planit.main.controller;

import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dao.Users;
import cteam.planit.main.services.SigninService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/signin")
public class SigninController
{
	@Autowired
	public SigninService service;

	@GetMapping("/signin")
	public List<Users> signin() throws Exception
	{
		return service.signin();
	}
}