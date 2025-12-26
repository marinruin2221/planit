package cteam.planit.main.dto;

import cteam.planit.main.entity.Accommodation;
import lombok.Data;
import java.util.List;

@Data
public class AIResponseDTO {
  private String response;
  private List<Accommodation> recommendations;
}
