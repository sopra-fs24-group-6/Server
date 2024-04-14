package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PasswordDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserIdDTO;
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

    @GetMapping("/lobby/{username}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO findLobbyByUsername(@PathVariable String username) {
        // Use the LobbyService to find the lobby by username
        Lobby foundLobby = lobbyService.findLobbyByUsername(username);

        // Convert the Lobby entity to LobbyGetDTO
        return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(foundLobby);
    }


    @GetMapping("/lobbies/{lobbyId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public LobbyGetDTO getLobbyById(@PathVariable("lobbyId") Long lobbyId) {
    // fetch lobby in the internal representation
    Lobby lobby = lobbyService.getLobbyById(lobbyId);
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
  }

  @PutMapping("/lobbies/{lobbyId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateLobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody LobbyPostDTO lobbyPostDTO) {
    // convert API user to internal representation
    Lobby lobbyInput = LobbyDTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);
    // update lobby
    lobbyService.updateLobby(lobbyId, lobbyInput);
  }

  @PostMapping("/lobbies/{lobbyId}/players")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public LobbyGetDTO joinLobby(@PathVariable("lobbyId") Long lobbyId, @RequestBody UserIdDTO userIdDTO) {
    // add player to lobby
    Lobby updatedLobby = lobbyService.addPlayerToLobby(lobbyId, userIdDTO.getUserId());
    return LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(updatedLobby);
  }

  @DeleteMapping("/lobbies/{lobbyId}/players/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void kickPlayer(@PathVariable("lobbyId") Long lobbyId,
                         @PathVariable("userId") Long targetUserId,
                         @RequestBody UserIdDTO requesterIdDTO) {
    lobbyService.kickPlayerFromLobby(lobbyId, targetUserId, requesterIdDTO.getUserId());
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
