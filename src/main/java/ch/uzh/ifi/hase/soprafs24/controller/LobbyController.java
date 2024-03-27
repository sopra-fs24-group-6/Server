package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.LobbyDTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.http.HttpStatus;
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
  public LobbyGetDTO createUser(@RequestBody LobbyPostDTO lobbyPostDTO) {
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
  public List<LobbyGetDTO> getAllLobbies() {
    // fetch all lobbies in the internal representation
    List<Lobby> lobbies = lobbyService.getLobbies();
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

}
