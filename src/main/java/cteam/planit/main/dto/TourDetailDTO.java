package cteam.planit.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourDetailDTO {
  // 기본 정보
  private String contentid;
  private String contenttypeid;
  private String title;

  // 주소 정보
  private String addr1;
  private String addr2;
  private String zipcode;

  // 이미지 정보
  private String firstimage;
  private String firstimage2;

  // 좌표 정보
  private String mapx;
  private String mapy;
  private String mlevel;

  // 연락처 및 홈페이지
  private String tel;
  private String homepage;

  // 상세 설명
  private String overview;

  // 지역 코드
  private String areacode;
  private String sigungucode;

  // 분류 코드
  private String cat1;
  private String cat2;
  private String cat3;

  // 시간 정보
  private String createdtime;
  private String modifiedtime;

  // 저작권 정보
  private String cpyrhtDivCd;
}
