package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;

import java.util.Set;


public class LobbyGetDTO {

  private Long id;

  private String name;

  private LobbyType type;

  private LobbyStatus status;

  private Set<PlayerDTO> players;

  private Integer numPlayers;

  private String themeName;

  private Integer gameDuration;

  private Long hostUserId;


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

  public LobbyType getType() {
    return type;
  }

  public void setType(LobbyType type) {
    this.type = type;
  }

  public LobbyStatus getStatus() {
    return status;
  }

  public void setStatus(LobbyStatus status) {
    this.status = status;
  }

  public Set<PlayerDTO> getPlayers() {
    return players;
  }

  public void setPlayers(Set<PlayerDTO> players) {
    this.players = players;
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
