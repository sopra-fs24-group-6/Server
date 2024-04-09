package ch.uzh.ifi.hase.soprafs24.websocket.dto;

import java.util.List;

public class ResultNotification {
  private String winnerRole;
  private List<Long> winners;
  private List<Long> losers;

  public String getWinnerRole() {
    return winnerRole;
  }

  public void setWinnerRole(String winnerRole) {
    this.winnerRole = winnerRole;
  }

  public List<Long> getWinners() {
    return winners;
  }

  public void setWinners(List<Long> winners) {
    this.winners = winners;
  }

  public List<Long> getLosers() {
    return losers;
  }

  public void setLosers(List<Long> losers) {
    this.losers = losers;
  }
}
