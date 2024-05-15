package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;


public class LobbyGetDTO extends LobbyBaseDTO {

  private Long id;

  private String lobbyAdmin;

  private Boolean isPrivate;

  private List<PlayerDTO> players;

  private String status;

  private Integer playerCount;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Integer getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(Integer playerCount) {
    this.playerCount = playerCount;
  }
}
