package cteam.planit.main.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSearchResponse {
  private List<GoogleSearchItem> items;

  public List<GoogleSearchItem> getItems() {
    return items;
  }

  public void setItems(List<GoogleSearchItem> items) {
    this.items = items;
  }
}
