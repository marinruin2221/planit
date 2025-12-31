package cteam.planit.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RATING")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {

  @Id
  @Column(name = "content_id", nullable = false, unique = true)
  private String contentId;

  @Column(name = "average_score")
  private Double averageScore;

  @Column(name = "review_count")
  private Integer reviewCount;

  @Column(name = "label", length = 50)
  private String label;

  @Column(name = "color_class", length = 50)
  private String colorClass;
}
