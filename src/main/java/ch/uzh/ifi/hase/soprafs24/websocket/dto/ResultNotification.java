package ch.uzh.ifi.hase.soprafs24.websocket.dto;

import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;

import java.util.List;

public class ResultNotification {
  private String winnerRole;
  private List<PlayerDTO> winners;
  private List<PlayerDTO> losers;

  public String getWinnerRole() {
    return winnerRole;
  }

  public void setWinnerRole(String winnerRole) {
    this.winnerRole = winnerRole;
  }

  public List<PlayerDTO> getWinners() {
    return winners;
  }

  public void setWinners(List<PlayerDTO> winners) {
    this.winners = winners;
  }

  public List<PlayerDTO> getLosers() {
    return losers;
  }

  public void setLosers(List<PlayerDTO> losers) {
    this.losers = losers;
  }
}
