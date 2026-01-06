package cteam.planit.main.dto;

import lombok.Data;

@Data
public class ReviewDTO
{
	private Long id;			// 여행 리뷰 시퀀스
	private String userId;		// 여행 리뷰 사용자 ID
	private String name;		// 여행 리뷰 이름
	private String date;		// 여행 리뷰 날짜
	private String stars;		// 여행 리뷰 별점
	private String level;		// 여행 리뷰 레벨
	private String deleteYN;	// 여행 리뷰 삭제 여부 (Y:삭제됨, N:삭제안됨)

	private String word;		// 검색
	private int page;			// 페이징
	private int size;			// 사이즈
}