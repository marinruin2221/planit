package cteam.planit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cteam.planit.main.dao.Users;
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
		Users users = new Users();
		users.setUserId("user01");
		users.setUserPw("pw1234");
		users.setName("성원");
		users.setEmail("user1@test.com");
		users.setBirthY("1995");
		users.setBirthM("05");
		users.setBirthD("20");
		users.setGender("M");
		users.setIsActive(1);

		usersRepository.save(users);
	}
}