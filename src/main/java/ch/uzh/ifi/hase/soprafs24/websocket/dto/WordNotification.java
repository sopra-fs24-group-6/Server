package ch.uzh.ifi.hase.soprafs24.websocket.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Role;

public class WordNotification {
    private String word;

    private Role role;

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
}