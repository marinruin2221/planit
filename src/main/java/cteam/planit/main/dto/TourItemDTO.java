package cteam.planit.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TourItemDTO {
  // 주소 정보
  private String addr1;
  private String addr2;
  private String zipcode;

  // 지역 및 분류 코드 (연산이 필요 없으므로 String 권장)
  private String areacode;
  private String sigungucode;
  private String cat1;
  private String cat2;
  private String cat3;

  // 콘텐츠 ID 및 타입
  private String contentid;
  private String contenttypeid;

  // 제목 및 연락처
  private String title;
  private String tel;

  // 이미지 URL
  private String firstimage;
  private String firstimage2;

  // 저작권 유형
  private String cpyrhtDivCd;

  // 좌표 정보 (JSON에서 문자열로 오므로 String으로 받고, 사용 시 Double.parseDouble 권장)
  private String mapx;
  private String mapy;
  private String mlevel;

  // 시간 정보 (YYYYMMDDHHMMSS 형식)
  private String createdtime;
  private String modifiedtime;

  // 행정동 코드 (추가 정보)
  private String lDongRegnCd;
  private String lDongSignguCd;

  // 대분류 시스템 코드 (추가 정보)
  private String lclsSystm1;
  private String lclsSystm2;
  private String lclsSystm3;

  // Getter, Setter, toString 생성 필요
  // (Lombok 사용 시 클래스 위에 @Data 어노테이션 추가)
}
