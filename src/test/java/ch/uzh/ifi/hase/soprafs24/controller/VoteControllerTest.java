package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Result;
import ch.uzh.ifi.hase.soprafs24.entity.Vote;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VoteDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.VoteDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;


public class VoteControllerTest {

  @Mock
  private VoteService voteService;

  @Mock
  private GameService gameService;

  @InjectMocks
  private VoteController voteController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void saveVoteAndNotifyResult_AllPlayersVoted() {
    // given
    Long lobbyId = 1L;
    VoteDTO voteDTO = new VoteDTO();
    voteDTO.setVoterUserId(1L);
    voteDTO.setVotedUserId(2L);
    Game game = new Game();
    game.setCurrentRound(1);
    Result result = new Result();
    result.setLobbyId(lobbyId);

    when(gameService.getActiveGameByLobbyId(lobbyId)).thenReturn(game);
    doNothing().when(voteService).saveVote(any());
    when(voteService.calculateResults(game)).thenReturn(Optional.of(result));

    // when
    voteController.saveVoteAndNotifyResult(lobbyId, voteDTO);

    // then
    verify(voteService, times(1)).saveVote(any());
    verify(gameService, times(1)).notifyResults(lobbyId, result);
  }

}