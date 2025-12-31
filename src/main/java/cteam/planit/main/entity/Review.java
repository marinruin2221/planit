package cteam.planit.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "REVIEW")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
  @SequenceGenerator(name = "review_seq", sequenceName = "REVIEW_SEQ", allocationSize = 1)
  @Column(name = "id")
  private Long id;

  @Column(name = "content_id", nullable = false)
  private String contentId;

  @Column(name = "reviewer_name", length = 100)
  private String reviewerName;

  @Column(name = "reviewer_level")
  private Integer reviewerLevel;

  @Column(name = "stars")
  private Integer stars;

  @Column(name = "content", length = 2000)
  private String content;

  @Column(name = "review_date")
  private LocalDate reviewDate;
}
