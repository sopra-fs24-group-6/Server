package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class PlayerDTO {

  private Long userId;

  private String username;

  private Boolean isHost;


  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Boolean getHost() {
    return isHost;
  }

  public void setHost(Boolean host) {
    isHost = host;
  }
}
