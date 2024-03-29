package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.util.Date;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private UserStatus status;
  private String password;
  private Date creation_date;
  private Date birthDate;

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getCreationDate() { return creation_date; }

  public void setCreationDate(Date creation_date) { this.creation_date = creation_date; }

  public Date getBirthDate() { return birthDate; }

  public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
}
