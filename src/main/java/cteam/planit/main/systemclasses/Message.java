package cteam.planit.main.systemclasses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  public String sender;
  public String content;
  public LocalDateTime time;
}
