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

  @Column(nullable = true)
  private Language language;

  @Column(nullable = false)
  private Boolean isHost;

  @ManyToOne
  @JoinColumn(name = "lobby_id")
  private Lobby lobby;


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

  public Language getLanguage() {
    return language;
  }

  public void setLanguage(Language language) {
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
}
