package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Role;

import java.util.List;

public class Result {
  private Long lobbyId;
  private List<Player> winnerPlayers;
  private List<Player> loserPlayers;
  private Role winnerRole;

  public Long getLobbyId() {
    return lobbyId;
  }

  public void setLobbyId(Long lobbyId) {
    this.lobbyId = lobbyId;
  }

  public List<Player> getWinnerPlayers() {
    return winnerPlayers;
  }

  public void setWinnerPlayers(List<Player> winnerPlayers) {
    this.winnerPlayers = winnerPlayers;
  }

  public List<Player> getLoserPlayers() {
    return loserPlayers;
  }

  public void setLoserPlayers(List<Player> loserPlayers) {
    this.loserPlayers = loserPlayers;
  }

  public Role getWinnerRole() {
    return winnerRole;
  }

  public void setWinnerRole(Role winnerRole) {
    this.winnerRole = winnerRole;
  }
}
