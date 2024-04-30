package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;

import java.util.List;


public class LobbyGetDTO {

  private Long id;

  private String name;

  private String lobbyAdmin;

  private Boolean isPrivate;

  private List<PlayerDTO> players;

  private String status;

  private Integer playerLimit;

  private Integer playerCount;

  private List<String> themes;

  private Integer rounds;

  private Integer roundTimer;

  private Integer clueTimer;

  private Integer discussionTimer;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLobbyAdmin() {
    return lobbyAdmin;
  }

  public void setLobbyAdmin(String lobbyAdmin) {
    this.lobbyAdmin = lobbyAdmin;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public List<PlayerDTO> getPlayers() {
    return players;
  }

  public void setPlayers(List<PlayerDTO> players) {
    this.players = players;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getPlayerLimit() {
    return playerLimit;
  }

  public void setPlayerLimit(Integer playerLimit) {
    this.playerLimit = playerLimit;
  }

  public Integer getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(Integer playerCount) {
    this.playerCount = playerCount;
  }

  public List<String> getThemes() {
    return themes;
  }

  public void setThemes(List<String> themes) {
    this.themes = themes;
  }

  public Integer getRounds() {
    return rounds;
  }

  public void setRounds(Integer rounds) {
    this.rounds = rounds;
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
