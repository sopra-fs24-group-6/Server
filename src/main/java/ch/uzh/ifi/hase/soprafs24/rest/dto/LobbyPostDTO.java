package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;


public class LobbyPostDTO {

  private Long lobbyAdmin;

  private String name;

  private String password;

  private Integer playerLimit;

  private List<String> themes;

  private Integer roundTimer;

  private Integer clueTimer;

  private Integer discussionTimer;


  public Long getLobbyAdmin() {
    return lobbyAdmin;
  }

  public void setLobbyAdmin(Long lobbyAdmin) {
    this.lobbyAdmin = lobbyAdmin;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getPlayerLimit() {
    return playerLimit;
  }

  public void setPlayerLimit(Integer playerLimit) {
    this.playerLimit = playerLimit;
  }

  public List<String> getThemes() {
    return themes;
  }

  public void setThemes(List<String> themes) {
    this.themes = themes;
  }

  public Integer getRoundTimer() {
    return roundTimer;
  }

  public void setRoundTimer(Integer roundTimer) {
    this.roundTimer = roundTimer;
  }

  public Integer getClueTimer() {
    return clueTimer;
  }

  public void setClueTimer(Integer clueTimer) {
    this.clueTimer = clueTimer;
  }

  public Integer getDiscussionTimer() {
    return discussionTimer;
  }

  public void setDiscussionTimer(Integer discussionTimer) {
    this.discussionTimer = discussionTimer;
  }
}
