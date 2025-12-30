package cteam.planit.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.Users;
import cteam.planit.main.dao.UsersRepository;

@Service
public class SigninService
{
	@Autowired
    public UsersRepository usersRepository;

	public List<Users> signin() throws Exception
	{
        return usersRepository.findAll();
	}
}