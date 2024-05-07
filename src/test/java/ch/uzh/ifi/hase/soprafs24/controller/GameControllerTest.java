package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Result;
import ch.uzh.ifi.hase.soprafs24.rest.dto.VoteDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.service.VoteService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.GameStartMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;


public class GameControllerTest {

  @Mock
  private LobbyService lobbyService;

  @Mock
  private GameService gameService;

  @InjectMocks
  private GameController gameController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  public void startGame_validInput_success() {
    // given
    GameStartMessage gameStartMessage = new GameStartMessage();
    gameStartMessage.setLobbyId(1L);
    gameStartMessage.setUserId(1L);

    doNothing().when(lobbyService).updateLobbyStatus(anyLong(), any());
    doNothing().when(gameService).startGame(anyLong(), anyLong());

    // when
    gameController.startGame(gameStartMessage);

    // then
    verify(lobbyService).updateLobbyStatus(eq(gameStartMessage.getLobbyId()), eq(LobbyStatus.IN_PROGRESS));
    verify(gameService).startGame(eq(gameStartMessage.getLobbyId()), eq(gameStartMessage.getUserId()));
  }

}