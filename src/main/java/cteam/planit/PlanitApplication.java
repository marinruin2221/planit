package cteam.planit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;

@SpringBootApplication
public class PlanitApplication implements CommandLineRunner
{
	@Autowired
	private UsersRepository usersRepository;

	public static void main(String[] args)
	{
		SpringApplication.run(PlanitApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		if(usersRepository.count() > 0) return;

		UsersDAO usersDAO = new UsersDAO();
		usersDAO.setUserId("marinruin");
		usersDAO.setUserPw("sk383412@A");
		usersDAO.setName("성원");
		usersDAO.setEmail("user1@test.com");
		usersDAO.setBirthY("1995");
		usersDAO.setBirthM("05");
		usersDAO.setBirthD("20");
		usersDAO.setGender("M");
		usersDAO.setIsActive(1);

		usersRepository.save(usersDAO);
	}
}