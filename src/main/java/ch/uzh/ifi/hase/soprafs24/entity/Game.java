package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.List;

public class Game {
  private Long lobbyId;
  private Integer roundTimer;
  private Integer clueTimer;
  private Integer discussionTimer;
  private Boolean isCluePhase;
  private List<Player> players;
  private List<String> themeNames;
  private Integer rounds;
  private Integer currentRound = 1;

  //For testing
    @Override
    public String toString() {
        return "Game{\n" +
                "\tlobbyId=" + lobbyId + ",\n" +
                "\troundTimer=" + roundTimer + ",\n" +
                "\tclueTimer=" + clueTimer + ",\n" +
                "\tdiscussionTimer=" + discussionTimer + "\n" +
                '}';
    }

  public void incrementAndSetCurrentRound() {
    this.currentRound += 1;
  }

  public Long getLobbyId() {
    return lobbyId;
  }

  public void setLobbyId(Long lobbyId) {
    this.lobbyId = lobbyId;
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

  public Boolean getIsCluePhase() {
    return isCluePhase;
  }

  public void setIsCluePhase(Boolean isCluePhase) {
    this.isCluePhase = isCluePhase;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public List<String> getThemeNames() {
    return themeNames;
  }

  public void setThemeNames(List<String> themeNames) {
    this.themeNames = themeNames;
  }

  public Integer getRounds() {
    return rounds;
  }

  public void setRounds(Integer rounds) {
    this.rounds = rounds;
  }

  public Integer getCurrentRound() {
    return currentRound;
  }

  public void setCurrentRound(Integer currentRound) {
    this.currentRound = currentRound;
  }
}
