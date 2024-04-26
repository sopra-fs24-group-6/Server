package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.GameStartMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

  private final GameService gameService;

  @Autowired
  GameController(GameService gameService) {
    this.gameService = gameService;
  }


  @MessageMapping("/startGame")
  public void startGame(@Payload GameStartMessage message) {
    System.out.printf("lobbyId:"+ message.getLobbyId()+ "userId:"+ message.getUserId());
    gameService.startGame(message.getLobbyId(), message.getUserId());
  }

}