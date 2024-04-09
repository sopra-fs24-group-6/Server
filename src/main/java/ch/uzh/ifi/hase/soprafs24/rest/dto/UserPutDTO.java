package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.Date;

public class UserPutDTO {

  private String username;

  private Date birthDate;

  private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }
}
