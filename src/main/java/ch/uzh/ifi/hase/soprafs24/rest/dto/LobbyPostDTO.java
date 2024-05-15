package ch.uzh.ifi.hase.soprafs24.rest.dto;


public class LobbyPostDTO extends LobbyBaseDTO {

  private Long lobbyAdmin;

  private String password;

  public Long getLobbyAdmin() {
    return lobbyAdmin;
  }

  public void setLobbyAdmin(Long lobbyAdmin) {
    this.lobbyAdmin = lobbyAdmin;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
