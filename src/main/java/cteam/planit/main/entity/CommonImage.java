package cteam.planit.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COMMON_IMAGE")
@SequenceGenerator(name = "common_image_seq_gen", sequenceName = "COMMON_IMAGE_SEQ", allocationSize = 1)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonImage {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "common_image_seq_gen")
  private Long id;

  @Column(nullable = false, unique = true)
  private String category; // e.g., "hotel", "pension", "camping"

  @Column(nullable = false)
  private String fileName; // e.g., "hotel_default.png"

  @Lob
  @Column(columnDefinition = "BLOB")
  private byte[] imageData;
}
