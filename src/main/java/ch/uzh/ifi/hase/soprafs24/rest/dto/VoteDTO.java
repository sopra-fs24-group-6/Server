package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class VoteDTO {
    private Long voterUserId;  // The user ID of the voter
    private Long votedUserId;  // The user ID of the voted player

    // Getters and Setters
    public Long getVoterUserId() {
        return voterUserId;
    }

    public void setVoterUserId(Long voterUserId) {
        this.voterUserId = voterUserId;
    }

    public Long getVotedUserId() {
        return votedUserId;
    }

    public void setVotedUserId(Long votedUserId) {
        this.votedUserId = votedUserId;
    }
}
