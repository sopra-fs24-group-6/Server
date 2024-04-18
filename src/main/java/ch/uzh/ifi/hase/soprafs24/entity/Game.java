package ch.uzh.ifi.hase.soprafs24.entity;

public class Game {
  Long lobbyId;
  Integer roundTimer;
  Integer clueTimer;
  Integer discussionTimer;

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
}
