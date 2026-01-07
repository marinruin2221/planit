package cteam.planit.main.dto;

import lombok.Data;

@Data
public class AccommodationReviewDTO
{
	private String contentId;
	private String title;
	private String addr1;
	private String addr2;
	private String cat3;
	private String firstimage;
	private Integer minPrice;
	private Long reviewCount;

    // ⭐ Native Query 전용 생성자 (순서 중요)
    public AccommodationReviewDTO(
            String contentId,
            String title,
            String addr1,
            String addr2,
            String cat3,
            String firstimage,
            Integer minPrice,
            Long reviewCount
    ) {
        this.contentId = contentId;
        this.title = title;
        this.addr1 = addr1;
        this.addr2 = addr2;
        this.cat3 = cat3;
        this.firstimage = firstimage;
        this.minPrice = minPrice;
        this.reviewCount = reviewCount;
    }
}