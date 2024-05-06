package ch.uzh.ifi.hase.soprafs24.websocket.dto;

public class EventNotification {
  private String eventType;
  private Integer currentRound;
  private Integer maxRound;

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public Integer getCurrentRound() {
    return currentRound;
  }

  public void setCurrentRound(Integer currentRound) {
    this.currentRound = currentRound;
  }

  public Integer getMaxRound() {
    return maxRound;
  }

  public void setMaxRound(Integer maxRound) {
    this.maxRound = maxRound;
  }
}