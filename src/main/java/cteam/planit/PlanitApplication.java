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
		usersDAO.setEmail("marinruin@naver.com");
		usersDAO.setBirthY("2000");
		usersDAO.setBirthM("01");
		usersDAO.setBirthD("14");
		usersDAO.setGender("M");
		usersDAO.setDeleteYN("N");
		usersRepository.save(usersDAO);

		if(breakdownRepository.count() > 5) return;

		BreakdownDAO breakdownDAO1 = new BreakdownDAO();
		breakdownDAO1.setUsersId(Long.valueOf("1"));
		breakdownDAO1.setName("서울 강남 호텔1");
		breakdownDAO1.setDateF("2025.01.05");
		breakdownDAO1.setDateT("2025.01.07");
		breakdownDAO1.setPrice("150000");
		breakdownDAO1.setStatus("1");
		breakdownDAO1.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO1);

		BreakdownDAO breakdownDAO2 = new BreakdownDAO();
		breakdownDAO2.setUsersId(Long.valueOf("1"));
		breakdownDAO2.setName("서울 강남 호텔2");
		breakdownDAO2.setDateF("2025.01.05");
		breakdownDAO2.setDateT("2025.01.07");
		breakdownDAO2.setPrice("150000");
		breakdownDAO2.setStatus("2");
		breakdownDAO2.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO2);

		BreakdownDAO breakdownDAO3 = new BreakdownDAO();
		breakdownDAO3.setUsersId(Long.valueOf("1"));
		breakdownDAO3.setName("서울 강남 호텔3");
		breakdownDAO3.setDateF("2025.01.05");
		breakdownDAO3.setDateT("2025.01.07");
		breakdownDAO3.setPrice("150000");
		breakdownDAO3.setStatus("3");
		breakdownDAO3.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO3);

		BreakdownDAO breakdownDAO4 = new BreakdownDAO();
		breakdownDAO4.setUsersId(Long.valueOf("1"));
		breakdownDAO4.setName("서울 강남 호텔4");
		breakdownDAO4.setDateF("2025.01.05");
		breakdownDAO4.setDateT("2025.01.07");
		breakdownDAO4.setPrice("150000");
		breakdownDAO4.setStatus("3");
		breakdownDAO4.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO4);

		BreakdownDAO breakdownDAO5 = new BreakdownDAO();
		breakdownDAO5.setUsersId(Long.valueOf("1"));
		breakdownDAO5.setName("서울 강남 호텔5");
		breakdownDAO5.setDateF("2025.01.05");
		breakdownDAO5.setDateT("2025.01.07");
		breakdownDAO5.setPrice("150000");
		breakdownDAO5.setStatus("3");
		breakdownDAO5.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO5);

		BreakdownDAO breakdownDAO6 = new BreakdownDAO();
		breakdownDAO6.setUsersId(Long.valueOf("1"));
		breakdownDAO6.setName("서울 강남 호텔6");
		breakdownDAO6.setDateF("2025.01.05");
		breakdownDAO6.setDateT("2025.01.07");
		breakdownDAO6.setPrice("150000");
		breakdownDAO6.setStatus("3");
		breakdownDAO6.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO6);

		BreakdownDAO breakdownDAO7 = new BreakdownDAO();
		breakdownDAO7.setUsersId(Long.valueOf("2"));
		breakdownDAO7.setName("서울 강남 호텔7");
		breakdownDAO7.setDateF("2025.01.05");
		breakdownDAO7.setDateT("2025.01.07");
		breakdownDAO7.setPrice("150000");
		breakdownDAO7.setStatus("3");
		breakdownDAO7.setDeleteYN("N");
		breakdownRepository.save(breakdownDAO7);
	}
}