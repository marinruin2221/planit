package cteam.planit.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ACCOMMODATION")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Accommodation {

  @Id
  @Column(name = "content_id", nullable = false, unique = true)
  private String contentId;

  @Column(name = "title", length = 500)
  private String title;

  @Column(name = "addr1", length = 500)
  private String addr1;

  @Column(name = "addr2", length = 500)
  private String addr2;

  @Column(name = "zipcode", length = 20)
  private String zipcode;

  @Column(name = "areacode", length = 10)
  private String areacode;

  @Column(name = "sigungucode", length = 10)
  private String sigungucode;

  @Column(name = "cat1", length = 10)
  private String cat1;

  @Column(name = "cat2", length = 10)
  private String cat2;

  @Column(name = "cat3", length = 20)
  private String cat3;

  @Column(name = "contenttypeid", length = 10)
  private String contenttypeid;

  @Column(name = "tel", length = 100)
  private String tel;

  @Column(name = "firstimage", length = 1000)
  private String firstimage;

  @Column(name = "firstimage2", length = 1000)
  private String firstimage2;

  @Column(name = "mapx", length = 50)
  private String mapx;

  @Column(name = "mapy", length = 50)
  private String mapy;

  @Column(name = "mlevel", length = 10)
  private String mlevel;

  @Column(name = "createdtime", length = 20)
  private String createdtime;

  @Column(name = "modifiedtime", length = 20)
  private String modifiedtime;

  @Column(name = "min_price")
  private Integer minPrice;
}
