package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String token;

  @Column(nullable = false)
  private UserStatus status;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Date creationDate;

  @Column(nullable = true)
  private Date birthDate;

  @Column(nullable = false)
  private String language;

  @Column(nullable = false)
  private int wins = 0;

  @Column(nullable = false)
  private int losses = 0;

  @Column(nullable = false)
  private double winlossratio = 0.0;

    // testing purposes
    @Override
    public String toString() {
        return "User{" +
                "\n\tid=" + id +
                ", \n\tusername='" + username + '\'' +
                ", \n\ttoken='" + token + '\'' +
                ", \n\tstatus=" + status +
                ", \n\tpassword='" + password + '\'' +
                ", \n\tcreationDate=" + creationDate +
                ", \n\tbirthDate=" + birthDate +
                ", \n\tlanguage='" + language + '\'' +
                ", \n\twins=" + wins +
                ", \n\tlosses=" + losses +
                ", \n\twinlossratio=" + winlossratio +
                "\n}";
    }

    public double getWinlossratio() {
        return winlossratio;
    }

    public void setWinlossratio(double winlossratio) {
        this.winlossratio = winlossratio;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void addWins() {
        this.wins += 1;
    }

    public void addLosses() {
        this.losses += 1;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int lose) {
        this.losses = lose;
    }

    public String getLanguage() {
      return language;
  }

    public void setLanguage(String language) {
        this.language = language;
    }

    @OneToOne
  @JoinColumn(name = "player_id")
  private Player player;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getCreationDate() { return creationDate; }

  public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

  public Date getBirthDate() { return birthDate; }

  public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
}
