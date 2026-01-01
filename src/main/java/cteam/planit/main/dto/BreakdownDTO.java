package cteam.planit.main.dto;

import lombok.Data;

@Data
public class BreakdownDTO
{
	private Long id;			// 예약 내역 시퀀스
	private Long usersId;		// 예약 내역 사용자 시퀀스
	private String name;		// 예약 내역 이름
	private String dateF;		// 예약 내역 체크인
	private String dateT;		// 예약 내역 체크아웃
	private String price;		// 에약 내역 결제금액
	private String status;		// 예약 내역 상태 (1:예약완료, 2:삭제, 3:이용완료)
	private String deleteYN;	// 예약 내역 삭제 여부 (Y:삭제됨, N:삭제안됨)

	private String word;		// 검색
	private int page;			// 페이징
	private int size;			// 사이즈
}