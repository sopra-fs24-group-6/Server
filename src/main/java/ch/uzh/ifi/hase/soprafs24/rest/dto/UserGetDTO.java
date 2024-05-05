package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
  private String username;
  private UserStatus status;
  private Date creationDate;
  private Date birthDate;
  private String token;
  private String language;

  private int wins;

  private int losses;

  private double winlossratio;

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public double getWinlossratio() {
        return winlossratio;
    }

    public void setWinlossratio(double winlossratio) {
        this.winlossratio = winlossratio;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

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

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Date getCreationDate() { return creationDate; }

  public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

  public Date getBirthDate() { return birthDate; }

  public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
