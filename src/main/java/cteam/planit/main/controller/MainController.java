package cteam.planit.main.controller;

import org.springframework.web.bind.annotation.RestController;

import cteam.planit.main.dao.Event;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.services.MainService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/main")
public class MainController
{
	@Autowired
	public MainService service;

	@GetMapping("/eventSelect")
	public List<Event> eventSelect() throws Exception
	{
		return service.eventSelect();
	}

	@PostMapping("/bestGoodStay")
	public List<Accommodation> bestGoodStay() throws Exception
	{
		return service.bestGoodStay();
	}
}