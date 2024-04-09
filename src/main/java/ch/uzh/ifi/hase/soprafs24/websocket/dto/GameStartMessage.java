package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class GameStartMessage {
  private Long lobbyId;
  private Long userId;

  public Long getLobbyId() {
    return lobbyId;
  }

  public void setLobbyId(Long lobbyId) {
    this.lobbyId = lobbyId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}