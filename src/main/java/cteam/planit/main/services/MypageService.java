package cteam.planit.main.services;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.BreakdownDAO;
import cteam.planit.main.dao.BreakdownRepository;
import cteam.planit.main.dao.UsersDAO;
import cteam.planit.main.dao.UsersRepository;
import cteam.planit.main.dto.BreakdownDTO;
import cteam.planit.main.dto.InformationDTO;
import cteam.planit.main.dto.ReviewDTO;
import cteam.planit.main.entity.Review;
import cteam.planit.main.repository.ReviewRepository;
import jakarta.transaction.Transactional;

@Service
public class MypageService
{
	@Autowired
	public UsersRepository usersRepository;

	@Autowired
	public BreakdownRepository breakdownRepository;

	@Autowired
	public ReviewRepository reviewRepository;

	public InformationDTO information(InformationDTO informationDTO) throws Exception
	{
		InformationDTO data = new InformationDTO();
		Optional<UsersDAO> info = usersRepository.findByUserId(informationDTO.getUserId());

		if(info.isPresent())
		{
			data.setUserId(info.get().getUserId());
			data.setUserPw(info.get().getUserPw());
			data.setName(info.get().getName());
			data.setEmail(info.get().getEmail());
			data.setBirthY(info.get().getBirthY());
			data.setBirthM(info.get().getBirthM());
			data.setBirthD(info.get().getBirthD());
			data.setGender(info.get().getGender());
		}

		return data;
	}

	public InformationDTO informationUpdate(InformationDTO informationDTO) throws Exception
	{
    	Optional<UsersDAO> info = usersRepository.findByUserId(informationDTO.getUserId());

		if(info.isPresent())
		{
			UsersDAO user = info.get();

			if(usersRepository.existsByEmail(informationDTO.getEmail()))
			{
				informationDTO.setEmailOverlapYN("Y");
			}
			else
			{
				if(informationDTO.getName() != null) user.setName(informationDTO.getName());
				if(informationDTO.getEmail() != null) user.setEmail(informationDTO.getEmail());
				if(informationDTO.getBirthY() != null) user.setBirthY(informationDTO.getBirthY());
				if(informationDTO.getBirthM() != null) user.setBirthM(informationDTO.getBirthM());
				if(informationDTO.getBirthD() != null) user.setBirthD(informationDTO.getBirthD());
				if(informationDTO.getGender() != null) user.setGender(informationDTO.getGender());
	
				usersRepository.save(user);

				informationDTO.setEmailOverlapYN("N");
			}
		}

		return informationDTO;
	}

	@Transactional
	public Page<BreakdownDAO> breakdown(BreakdownDTO breakdownDTO) throws Exception
	{
		String today = LocalDate.now().toString();

		breakdownRepository.updateCompletedByCheckoutDate
		(
			breakdownDTO.getUserId(),
			today
		);

		PageRequest pageable = PageRequest.of(breakdownDTO.getPage(), breakdownDTO.getSize(),Sort.by(Sort.Direction.DESC, "id"));
		
		return breakdownRepository.findByUserIdAndDeleteYNAndNameContaining
		(
			breakdownDTO.getUserId(),
			"N",
			breakdownDTO.getWord(),
			pageable
		);
	}

	public void breakdownCreate(BreakdownDTO breakdownDTO) throws Exception
	{
		BreakdownDAO breakdown = new BreakdownDAO();
		breakdown.setContentId(breakdownDTO.getContentId());
		breakdown.setUserId(breakdownDTO.getUserId());
		breakdown.setName(breakdownDTO.getName());
		breakdown.setDateF(breakdownDTO.getDateF());
		breakdown.setDateT(breakdownDTO.getDateT());
		breakdown.setPrice(breakdownDTO.getPrice());
		breakdown.setStatus(breakdownDTO.getStatus());
		breakdown.setDeleteYN("N");

		breakdownRepository.save(breakdown);
	}

	public void breakdownCancel(BreakdownDTO breakdownDTO) throws Exception
	{
		Optional<BreakdownDAO> breakdown = breakdownRepository.findById(breakdownDTO.getId());

		if(breakdown.isPresent())
		{
			BreakdownDAO data = breakdown.get();
			data.setStatus("2");

			breakdownRepository.save(data);
		}
	}

	public Page<Review> review(ReviewDTO reviewDTO) throws Exception
	{
		PageRequest pageable = PageRequest.of(reviewDTO.getPage(), reviewDTO.getSize(),Sort.by(
			Sort.Order.desc("id"),
			Sort.Order.desc("reviewDate")
		));

		return reviewRepository.findByUserIdAndDeleteYNAndNameContaining
		(
			reviewDTO.getUserId(),
			"N",
			reviewDTO.getWord(),
			pageable
		);
	}

	public void reviewCreate(ReviewDTO reviewDTO) throws Exception
	{
		Review review = Review.builder()
			.contentId(reviewDTO.getContentId())
			.userId(reviewDTO.getUserId())
			.name(reviewDTO.getName())
			.reviewerName(reviewDTO.getNickname())
			.reviewerLevel(Integer.parseInt(reviewDTO.getLevel()))
			.stars(Integer.parseInt(reviewDTO.getStars()))
			.content(reviewDTO.getContent())
			.reviewDate(LocalDate.now())
			.deleteYN("N")
			.build();

		reviewRepository.save(review);
	}

	public void reviewUpdate(ReviewDTO reviewDTO) throws Exception
	{
		Optional<Review> info = reviewRepository.findById(reviewDTO.getId());

		if(info.isPresent())
		{
			Review review = info.get();

			if(reviewDTO.getStars() != null) review.setStars(Integer.parseInt(reviewDTO.getStars()));
			if(reviewDTO.getContent() != null) review.setContent(reviewDTO.getContent());

			reviewRepository.save(review);
		}
	}

	public void reviewDelete(ReviewDTO reviewDTO) throws Exception
	{
		Optional<Review> review = reviewRepository.findById(reviewDTO.getId());

		if(review.isPresent())
		{
			Review data = review.get();
			data.setDeleteYN("Y");

			reviewRepository.save(data);
		}
	}

	public void withdraw(InformationDTO informationDTO) throws Exception
	{
		Optional<UsersDAO> info = usersRepository.findByUserId(informationDTO.getUserId());

		if(info.isPresent())
		{
			UsersDAO user = info.get();

			user.setDeleteYN("Y");
			
			usersRepository.save(user);
		}
	}
}