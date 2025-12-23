package cteam.planit.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourIntroDTO {
  private String contentid;
  private String contenttypeid;

  // 숙박(32) 관련 필드
  private String checkintime; // 입실 시간
  private String checkouttime; // 퇴실 시간
  private String roomcount; // 객실 수
  private String reservationurl; // 예약 URL
  private String reservationlodging; // 예약 안내
  private String subfacility; // 부대시설
  private String parkinglodging; // 주차 시설
  private String infocenterlodging; // 문의 및 안내
  private String accomcountlodging; // 수용 인원
  private String refundregulation; // 환불 규정
}
