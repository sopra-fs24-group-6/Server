package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;

import java.io.Serializable;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private LobbyType type;

  @Column(nullable = false)
  private LobbyStatus status;

  @Column()
  private String password;

  @OneToMany(mappedBy = "lobby")
  private Set<Player> players = new HashSet<Player>();

  @Column(nullable = false)
  private Integer numPlayers;

  @ManyToOne
  @JoinColumn(name = "theme_id", nullable = false)
  private Theme theme;

  @Column()
  private Integer gameDuration;

  @Column(nullable = false)
  private Long hostUserId;


  public void addPlayer(Player player) {
    players.add(player);
    player.setLobby(this);
  }

  public void removePlayer(Player player) {
    players.remove(player);
    player.setLobby(null);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LobbyType getType() {
    return type;
  }

  public void setType(LobbyType type) {
    this.type = type;
  }

  public LobbyStatus getStatus() {
    return status;
  }

  public void setStatus(LobbyStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public void setPlayers(Set<Player> players) {
    this.players = players;
  }

  public Integer getNumPlayers() {
    return numPlayers;
  }

  public void setNumPlayers(Integer numPlayers) {
    this.numPlayers = numPlayers;
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }

  public Integer getGameDuration() {
    return gameDuration;
  }

  public void setGameDuration(Integer gameDuration) {
    this.gameDuration = gameDuration;
  }

  public Long getHostUserId() {
    return hostUserId;
  }

  public void setHostUserId(Long hostUserId) {
    this.hostUserId = hostUserId;
  }
}