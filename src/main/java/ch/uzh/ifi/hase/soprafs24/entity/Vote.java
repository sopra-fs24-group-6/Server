package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "VOTE")
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long lobbyId;

    @Column(nullable = false)
    private Integer round;

    @Column(nullable = false)
    private Long voterUserId;

    @Column(nullable = false)
    private Long votedUserId;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

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

