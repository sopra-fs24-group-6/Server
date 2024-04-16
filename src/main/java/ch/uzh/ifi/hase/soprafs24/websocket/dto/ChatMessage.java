package ch.uzh.ifi.hase.soprafs24.websocket.dto;
import java.time.Instant;

public class ChatMessage {
  private String content;
  private Long userId;
  private String username;
  private Instant timestamp;
  private Long lobbyId;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Long getlobbyId() {
    return lobbyId;
  }

  public void setlobbyId(Long lobbyId) {
    this.lobbyId = lobbyId;
  }
}