package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserAvatarDTO {
  private Long id;
  private String avatarUrl; // Assuming URL is generated after upload

  // Getter and Setter for userId

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // Getter and Setter for avatarUrl
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
