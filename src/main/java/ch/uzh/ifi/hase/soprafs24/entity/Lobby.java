package ch.uzh.ifi.hase.soprafs24.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;
import java.util.Timer;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;

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

    @Column()
    private String password;

    @Column(nullable = false)
    private List<User> players;

    @Column()
    private Integer playerLimit;

    @Column()
    private List<Theme> themes;

    @Column()
    private Timer roundTimer;

    @Column
    private Timer clueTimer;

    @Column
    private Timer discussionTimer;

}
