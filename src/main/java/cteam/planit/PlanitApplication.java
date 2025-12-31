package cteam.planit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cteam.planit.main.dao.BreakdownDAO;
import cteam.planit.main.dao.BreakdownRepository;
import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;

@SpringBootApplication
public class PlanitApplication implements CommandLineRunner
{
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private BreakdownRepository breakdownRepository;

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

		if(breakdownRepository.count() > 2) return;

		BreakdownDAO breakdownDAO = new BreakdownDAO();
		breakdownDAO.setUsersId(null);
		breakdownDAO.setName("서울 강남 호텔1");
		breakdownDAO.setDateF("2025.01.05");
		breakdownDAO.setDateT("2025.01.07");
		breakdownDAO.setStatus("1");
		breakdownDAO.setIsActive(1);

		breakdownRepository.save(breakdownDAO);

		breakdownDAO.setUsersId(null);
		breakdownDAO.setName("서울 강남 호텔2");
		breakdownDAO.setDateF("2025.01.05");
		breakdownDAO.setDateT("2025.01.07");
		breakdownDAO.setStatus("2");
		breakdownDAO.setIsActive(1);

		breakdownRepository.save(breakdownDAO);

		breakdownDAO.setUsersId(null);
		breakdownDAO.setName("서울 강남 호텔3");
		breakdownDAO.setDateF("2025.01.05");
		breakdownDAO.setDateT("2025.01.07");
		breakdownDAO.setStatus("3");
		breakdownDAO.setIsActive(1);

		breakdownRepository.save(breakdownDAO);
	}
}