package cteam.planit.main.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSearchItem {
  private String link;
  private String title;
  private String snippet;
  private Image image;

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSnippet() {
    return snippet;
  }

  public void setSnippet(String snippet) {
    this.snippet = snippet;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Image {
    private String contextLink;
    private String thumbnailLink;

    public String getContextLink() {
      return contextLink;
    }

    public void setContextLink(String contextLink) {
      this.contextLink = contextLink;
    }

    public String getThumbnailLink() {
      return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
      this.thumbnailLink = thumbnailLink;
    }
  }
}
