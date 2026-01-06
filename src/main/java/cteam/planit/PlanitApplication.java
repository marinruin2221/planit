package cteam.planit;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cteam.planit.main.dao.BreakdownDAO;
import cteam.planit.main.dao.BreakdownRepository;
import cteam.planit.main.entity.Review;
import cteam.planit.main.repository.ReviewRepository;

@SpringBootApplication
public class PlanitApplication implements CommandLineRunner
{
	@Autowired
	private BreakdownRepository breakdownRepository;

	@Autowired
	private ReviewRepository reviewRepository;

	public static void main(String[] args)
	{
		SpringApplication.run(PlanitApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		if(breakdownRepository.count() > 5) return;
		for(int i = 1; i <= 6; i++)
		{
			BreakdownDAO breakdownDAO = new BreakdownDAO();
			breakdownDAO.setUserId("marinruin");
			breakdownDAO.setContentId("1");
			breakdownDAO.setName("서울 강남 호텔" + i);
			breakdownDAO.setDateF("2025-01-05");
			breakdownDAO.setDateT("2025-01-07");
			breakdownDAO.setPrice("150000");
			breakdownDAO.setStatus("3");
			breakdownDAO.setDeleteYN("N");
			breakdownRepository.save(breakdownDAO);
		}

		if(reviewRepository.count() > 5) return;
		for(int i = 1; i <= 6; i++)
		{
			Review review = new Review();
			review.setContentId("1");
			review.setUserId("marinruin");
			review.setName("서울 강남 호텔" + i);
			review.setReviewerName("여행러버" + i);
			review.setReviewerLevel(17);
			review.setStars(3);
			review.setContent("나쁘지 않았지만 기대만큼은 아니었어요" + i);
			review.setReviewDate(LocalDate.of(2026, 01, 01));
			review.setDeleteYN("N");
			reviewRepository.save(review);
		}
	}
}