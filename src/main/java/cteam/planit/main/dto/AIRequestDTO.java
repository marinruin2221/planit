package cteam.planit.main.dto;

import lombok.Data;
import java.util.List;

@Data
public class AIRequestDTO {
  private String message;
  private List<MessagePart> history;

  @Data
  public static class MessagePart {
    private String role;
    private String text;
  }
}
