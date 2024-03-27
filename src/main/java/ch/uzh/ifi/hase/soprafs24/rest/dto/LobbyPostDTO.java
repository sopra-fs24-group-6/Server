package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;


public class LobbyPostDTO {

  private String name;

  private LobbyType type;

  private String password;

  private Integer numPlayers;

  private String themeName;

  private Integer gameDuration;

  private Long hostUserId;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LobbyType getType() {
    return type;
  }

  public void setType(LobbyType type) {
    this.type = type;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getNumPlayers() {
    return numPlayers;
  }

  public void setNumPlayers(Integer numPlayers) {
    this.numPlayers = numPlayers;
  }

  public String getThemeName() {
    return themeName;
  }

  public void setThemeName(String themeName) {
    this.themeName = themeName;
  }

  public Integer getGameDuration() {
    return gameDuration;
  }

  public void setGameDuration(Integer gameDuration) {
    this.gameDuration = gameDuration;
  }

  public Long getHostUserId() {
    return hostUserId;
  }

  public void setHostUserId(Long hostUserId) {
    this.hostUserId = hostUserId;
  }
}
