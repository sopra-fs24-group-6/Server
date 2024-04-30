package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LobbyDTOMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.PlayerDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class LobbyController {

  private final LobbyService lobbyService;

  LobbyController(LobbyService lobbyService) {
    this.lobbyService = lobbyService;
  }

  @PostMapping("/lobbies")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LobbyGetDTO createLobby(@RequestBody LobbyPostDTO lobbyPostDTO) {
    // convert API user to internal representation
    Lobby lobbyInput = LobbyDTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);
    // create lobby
    Lobby createdLobby = lobbyService.createLobby(lobbyInput);
    // convert internal representation of lobby back to API
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(createdLobby);
  }

  @GetMapping("/lobbies")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<LobbyGetDTO> getLobbies(@RequestParam(required = false) String username,
                                      @RequestParam(required = false) Long userId) {

    List<Lobby> lobbies = lobbyService.getLobbies(username, userId);

    List<LobbyGetDTO> lobbyGetDTOs = new ArrayList<>();
    // convert each lobby to the API representation
    for (Lobby lobby : lobbies) {
      lobbyGetDTOs.add(LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby));
    }
    return lobbyGetDTOs;
  }

  @GetMapping("/lobbies/{lobbyId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LobbyGetDTO getLobbyById(@PathVariable("lobbyId") Long lobbyId) {
    // fetch lobby in the internal representation
    Lobby lobby = lobbyService.getLobbyById(lobbyId);
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
  }


    @GetMapping("/lobbies/{lobbyId}/players")
    @ResponseBody
    public List<PlayerDTO> getPlayers(@PathVariable("lobbyId") Long lobbyId) {
        // fetch lobby in the internal representation
        List<Player> players = lobbyService.getPlayersById(lobbyId);
        List<PlayerDTO> playerDTOS = new ArrayList<>();
        for(Player player : players) {
            playerDTOS.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerDTO(player));
        }
        return playerDTOS;
    }

  @PutMapping("/lobbies/{lobbyId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateLobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody LobbyPostDTO lobbyPostDTO) {
    // convert API user to internal representation
    Lobby lobbyInput = LobbyDTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);
    // update lobby
    Lobby updatedLobby = lobbyService.updateLobby(lobbyId, lobbyInput);
    // send Lobby info to client side:
      LobbyGetDTO lobbyGetDTO = LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(updatedLobby);
      lobbyService.sendLobbyInfoToLobby(lobbyId, lobbyGetDTO);
      // send player list to client side:
      List<PlayerDTO> playerDTOS = new ArrayList<>();
      for(Player player : updatedLobby.getPlayers()) {
          playerDTOS.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerDTO(player));
      }
      lobbyService.sendPlayerListToLobby(playerDTOS, lobbyId);
  }

  @PostMapping("/lobbies/{lobbyId}/players")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LobbyGetDTO joinLobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody UserIdDTO userIdDTO) {
    // add player to lobby
    Lobby updatedLobby = lobbyService.addPlayerToLobby(lobbyId, userIdDTO.getUserId());
      List<PlayerDTO> playerDTOS = new ArrayList<>();
      for(Player player : updatedLobby.getPlayers()) {
          playerDTOS.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerDTO(player));
      }
      lobbyService.sendPlayerListToLobby(playerDTOS, updatedLobby.getId());
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(updatedLobby);
  }

  @DeleteMapping("/lobbies/{lobbyId}/players/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void kickPlayer(@PathVariable("lobbyId") Long lobbyId,
                         @PathVariable("userId") Long targetUserId,
                         @RequestBody UserIdDTO requesterIdDTO) {
    Lobby lobby = lobbyService.kickPlayerFromLobby(lobbyId, targetUserId, requesterIdDTO.getUserId());
      List<PlayerDTO> playerDTOS = new ArrayList<>();
      for(Player player : lobby.getPlayers()) {
          playerDTOS.add(PlayerDTOMapper.INSTANCE.convertEntityToPlayerDTO(player));
      }
      lobbyService.sendPlayerListToLobby(playerDTOS, lobby.getId());
  }

  @PostMapping("/lobbies/{lobbyId}/authentication")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void authenticateLobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody PasswordDTO passwordDTO) {
    // authenticate lobby with password
    lobbyService.authenticateLobby(lobbyId, passwordDTO.getPassword());
  }

  @GetMapping("/themes")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<String> getAllThemes() {
    // fetch all available themes
    return lobbyService.getThemes();
  }

}
