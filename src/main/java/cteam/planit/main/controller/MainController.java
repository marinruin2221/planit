package cteam.planit.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController
{
	@GetMapping("/api/message")
	public String message() throws Exception
	{
		return "Hello Main Page";
	}
}

