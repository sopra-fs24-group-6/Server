package ch.uzh.ifi.hase.soprafs24.websocket.dto;

import ch.uzh.ifi.hase.soprafs24.constant.Role;

public class WordNotification {
    private String word;
    private boolean isWolf;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Boolean getIsWolf() {
        return isWolf;
    }

    public void setisWolf(Boolean isWolf) {
        this.isWolf = isWolf;
    }

}