package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;

import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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
  private List<Player> players = new ArrayList<>();

  @Column(nullable = false)
  private Integer playerLimit;

  @Column(nullable = false)
  private Integer playerCount = 0;

  @ManyToMany
  @JoinTable(
    name = "LOBBY_THEME",
    joinColumns = @JoinColumn(name = "lobby_id"),
    inverseJoinColumns = @JoinColumn(name = "theme_id")
  )
  private List<Theme> themes = new ArrayList<>();

  @Column()
  private Integer rounds;

  @Column()
  private Integer roundTimer;

  @Column()
  private Integer clueTimer;

  @Column()
  private Integer discussionTimer;

  @OneToOne
  @JoinColumn(name = "host_id", nullable = false)
  private Player host;


  /*
  Additional methods
  */
  public void addPlayer(Player player) {
    players.add(player);
    player.setLobby(this);
    playerCount += 1;
  }

  public void removePlayer(Player player) {
    players.remove(player);
    playerCount -= 1;
  }

  public List<String> getThemeNames() {
    List<String> themeNames = new ArrayList<>();
    for (Theme theme : themes) {
      themeNames.add(theme.getName());
    }
    return themeNames;
  }


  /*
  Basic getter and setter
  */
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

  public List<Player> getPlayers() {
    return players;
  }

  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public Integer getPlayerLimit() {
    return playerLimit;
  }

  public void setPlayerLimit(Integer playerLimit) {
    this.playerLimit = playerLimit;
  }

  public Integer getPlayerCount() {
    return playerCount;
  }

  public void setPlayerCount(Integer playerCount) {
    this.playerCount = playerCount;
  }

  public List<Theme> getThemes() {
    return themes;
  }

  public void setThemes(List<Theme> themes) {
    this.themes = themes;
  }

  public Integer getRounds() {
    return rounds;
  }

  public void setRounds(Integer rounds) {
    this.rounds = rounds;
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

  public Player getHost() {
    return host;
  }

  public void setHost(Player host) {
    this.host = host;
  }
}