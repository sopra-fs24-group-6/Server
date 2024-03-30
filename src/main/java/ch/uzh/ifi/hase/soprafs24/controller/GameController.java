package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LobbyDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class GameController {

  private final LobbyService lobbyService;

  GameController(LobbyService lobbyService) {
    this.lobbyService = lobbyService;
  }


  @PostMapping("/games")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LobbyGetDTO startGame(@RequestBody Long lobbyId) {
    /*
      At this moment, just change status of the selected lobby.
      Need to implement create new game.
     */
    Lobby startedLobby = lobbyService.startGame(lobbyId);
    // convert internal representation of lobby back to API
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(startedLobby);
  }

}
