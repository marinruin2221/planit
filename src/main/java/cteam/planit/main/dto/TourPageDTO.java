package cteam.planit.main.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TourPageDTO {
  private List<TourItemDTO> items;
  private int totalCount;
}
