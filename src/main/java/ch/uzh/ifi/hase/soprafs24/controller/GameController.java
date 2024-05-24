package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.GameStartMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

  private final LobbyService lobbyService;
  private final GameService gameService;

  @Autowired
  GameController(LobbyService lobbyService, GameService gameService) {
    this.lobbyService = lobbyService;
    this.gameService = gameService;
  }
  

  @MessageMapping("/startGame")
  public void startGame(@Payload GameStartMessage message) {
    // update lobby status
    lobbyService.updateLobbyStatus(message.getLobbyId(), LobbyStatus.IN_PROGRESS);
    // start game
    gameService.startGame(message.getLobbyId(), message.getUserId());
  }

}