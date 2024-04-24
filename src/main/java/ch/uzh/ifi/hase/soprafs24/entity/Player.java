package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.constant.Language;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "PLAYER")
public class Player implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = true)
  private Role role;

  @Column(nullable = true)
  private String word;

  //Still think about whether it should be enum or string. If you are curious about how the enum should be, you can
    // visit the comment in Language, but for now, I'll make it string
  @Column(nullable = true)
  private String language;

  @Column(nullable = false)
  private Boolean isHost;

  @ManyToOne
  @JoinColumn(name = "lobby_id")
  private Lobby lobby;


    // For testing purposes
    @Override
    public String toString() {
        return "Player{" +
                "\n\tuserId=" + userId +
                ",\n\tusername='" + username + '\'' +
                ",\n\trole=" + role +
                ",\n\tword='" + word + '\'' +
                ",\n\tlanguage=" + language +
                ",\n\tisHost=" + isHost +
                ",\n\tlobbyId=" + (lobby != null ? lobby.getId() : "null") +
                "\n}";
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

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

    public Boolean getHost() {
    return isHost;
  }

  public void setHost(Boolean host) {
    isHost = host;
  }

  public Lobby getLobby() {
    return lobby;
  }

  public void setLobby(Lobby lobby) {
    this.lobby = lobby;
  }

    public Long getId() {
      return userId;
    }
}
