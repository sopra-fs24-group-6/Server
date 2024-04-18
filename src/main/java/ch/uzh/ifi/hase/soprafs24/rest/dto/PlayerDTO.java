package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Role;

import javax.persistence.Column;
import javax.persistence.Id;

public class PlayerDTO {

    private Long userId;

    private String username;

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

}
