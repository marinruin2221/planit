package cteam.planit.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 숙박 시설의 객실 상세 정보 DTO
 * detailInfo1 API 응답 매핑
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomInfoDTO {
  private String contentid;
  private String contenttypeid;

  // 객실 기본 정보
  private String roomtitle; // 객실명
  private String roomsize1; // 객실 크기 (평형)
  private String roomsize2; // 객실 크기 (제곱미터)
  private String roombasecount; // 기준 인원
  private String roommaxcount; // 최대 인원

  // 객실 이미지
  private String roomimg1; // 객실 이미지 1
  private String roomimg1alt; // 객실 이미지 1 대체 텍스트
  private String roomimg2; // 객실 이미지 2
  private String roomimg2alt; // 객실 이미지 2 대체 텍스트
  private String roomimg3; // 객실 이미지 3
  private String roomimg3alt; // 객실 이미지 3 대체 텍스트
  private String roomimg4; // 객실 이미지 4
  private String roomimg4alt; // 객실 이미지 4 대체 텍스트
  private String roomimg5; // 객실 이미지 5
  private String roomimg5alt; // 객실 이미지 5 대체 텍스트

  // 요금 정보
  private String roomoffseasonminfee1; // 비수기 주중 최소 요금
  private String roomoffseasonminfee2; // 비수기 주말 최소 요금
  private String roompeakseasonminfee1; // 성수기 주중 최소 요금
  private String roompeakseasonminfee2; // 성수기 주말 최소 요금

  // 편의시설 (Y/N)
  private String roombathfacility; // 욕실
  private String roomtv; // TV
  private String roompc; // PC
  private String roominternet; // 인터넷
  private String roomrefrigerator; // 냉장고
  private String roomaircondition; // 에어컨
  private String roomhairdryer; // 헤어드라이기
  private String roomsofa; // 소파
  private String roomcook; // 취사 가능 여부
  private String roomtable; // 테이블
  private String roomcable; // 케이블
}
