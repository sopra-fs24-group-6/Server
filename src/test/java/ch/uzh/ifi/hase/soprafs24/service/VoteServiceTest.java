package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Result;
import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class VoteServiceTest {

  @Mock
  private VoteRepository voteRepository;

  @InjectMocks
  private VoteService voteService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  public void saveVote_success() {
    // given
    Vote vote = new Vote();
    vote.setLobbyId(1L);
    vote.setRound(1);
    vote.setVoterUserId(1L);
    vote.setVotedUserId(2L);

    when(voteRepository.findByVoterUserIdAndLobbyId(any(), any()))
      .thenReturn(Optional.empty());

    // when
    voteService.saveVote(vote);

    // then
    Mockito.verify(voteRepository).save(vote);
    Mockito.verify(voteRepository).flush();
  }

  @Test
  public void calculateResults_allPlayersVoted_returnResult() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setCurrentRound(1);

    Player player1 = new Player();
    player1.setUserId(1L);
    player1.setRole(Role.WOLF);
    Player player2 = new Player();
    player2.setUserId(2L);
    player2.setRole(Role.VILLAGER);
    Player player3 = new Player();
    player3.setUserId(3L);
    player3.setRole(Role.VILLAGER);
    game.setPlayers(List.of(player1, player2, player3));

    Vote vote1 = new Vote();
    vote1.setVoterUserId(1L);
    vote1.setVotedUserId(2L);
    Vote vote2 = new Vote();
    vote2.setVoterUserId(2L);
    vote2.setVotedUserId(1L);
    Vote vote3 = new Vote();
    vote3.setVoterUserId(3L);
    vote3.setVotedUserId(1L);
    List<Vote> votesList = List.of(vote1, vote2, vote3);
    when(voteRepository.findByLobbyIdAndRound(anyLong(), anyInt())).thenReturn(votesList);

    // when
    Optional<Result> result = voteService.calculateResults(game);

    // then
    assertTrue(result.isPresent(), "Result should be not empty");
    assertEquals(Role.VILLAGER, result.get().getWinnerRole());
    assertEquals(2, result.get().getWinnerPlayers().size());
    assertEquals(1, result.get().getLoserPlayers().size());
  }

  @Test
  public void calculateResults_allPlayersVoted_tiedVoting_thenWolfWins() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setCurrentRound(1);

    Player player1 = new Player();
    player1.setUserId(1L);
    player1.setRole(Role.WOLF);
    Player player2 = new Player();
    player2.setUserId(2L);
    player2.setRole(Role.VILLAGER);
    Player player3 = new Player();
    player3.setUserId(3L);
    player3.setRole(Role.VILLAGER);
    game.setPlayers(List.of(player1, player2, player3));

    Vote vote1 = new Vote();
    vote1.setVoterUserId(1L);
    vote1.setVotedUserId(2L);
    Vote vote2 = new Vote();
    vote2.setVoterUserId(2L);
    vote2.setVotedUserId(3L);
    Vote vote3 = new Vote();
    vote3.setVoterUserId(3L);
    vote3.setVotedUserId(1L);
    List<Vote> votesList = List.of(vote1, vote2, vote3);
    when(voteRepository.findByLobbyIdAndRound(anyLong(), anyInt())).thenReturn(votesList);

    // when
    Optional<Result> result = voteService.calculateResults(game);

    // then
    assertTrue(result.isPresent(), "Result should be not empty");
    assertEquals(Role.WOLF, result.get().getWinnerRole());
    assertEquals(1, result.get().getWinnerPlayers().size());
    assertEquals(2, result.get().getLoserPlayers().size());
  }

  @Test
  public void calculateResults_notAllPlayersVoted_returnEmpty() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setCurrentRound(1);

    Player player1 = new Player();
    player1.setUserId(1L);
    player1.setRole(Role.WOLF);
    Player player2 = new Player();
    player2.setUserId(2L);
    player2.setRole(Role.VILLAGER);
    Player player3 = new Player();
    player3.setUserId(3L);
    player3.setRole(Role.VILLAGER);
    game.setPlayers(List.of(player1, player2, player3));

    Vote vote1 = new Vote();
    vote1.setVoterUserId(1L);
    vote1.setVotedUserId(2L);
    Vote vote2 = new Vote();
    vote2.setVoterUserId(2L);
    vote2.setVotedUserId(1L);
    List<Vote> votesList = List.of(vote1, vote2);
    when(voteRepository.findByLobbyIdAndRound(anyLong(), anyInt())).thenReturn(votesList);

    // when
    Optional<Result> result = voteService.calculateResults(game);

    // then
    assertTrue(result.isEmpty(), "Result should be empty");
  }

}
