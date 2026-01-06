package cteam.planit.main.dao;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BREAKDOWN")
@SequenceGenerator
(
	name = "breakdown_seq_gen",
	sequenceName = "BREAKDOWN_SEQ",
	allocationSize = 1
)
@Data
public class BreakdownDAO
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "breakdown_seq_gen")
	private Long id;			// 예약 내역 시퀀스
	private String contentId;   // 예약 내역 숙소 ID
	private String userId;		// 예약 내역 사용자 ID
	private String name;		// 예약 내역 이름
	private String dateF;		// 예약 내역 체크인
	private String dateT;		// 예약 내역 체크아웃
	private String price;		// 에약 내역 결제금액
	private String status;		// 예약 내역 상태 (1:예약완료, 2:삭제, 3:이용완료)
	private String deleteYN;	// 예약 내역 삭제 여부 (Y:삭제됨, N:삭제안됨)
}