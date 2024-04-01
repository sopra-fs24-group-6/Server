package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapping;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */

public class LobbyDTOMapperTest {

//  private Lobby testLobby;
//
//  @BeforeEach
//  public void setup() {
//    testLobby = new Lobby();
//    testLobby.setId(1L);
//    testLobby.setName("lobbyName");
//    testLobby.setPassword("password");
//    testLobby.setPlayerLimit(3);
//    testLobby.setRounds(3);
//    testLobby.setRoundTimer(60);
//    testLobby.setClueTimer(10);
//    testLobby.setDiscussionTimer(60);
//  }

  @Test
  public void createLobby_fromLobbyPostDTO_toLobby_success() {
    // given
    LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
    lobbyPostDTO.setLobbyAdmin(1L);
    lobbyPostDTO.setName("lobbyName");
    lobbyPostDTO.setPassword("password");
    lobbyPostDTO.setPlayerLimit(3);
    lobbyPostDTO.setThemes(List.of("Theme1", "Theme2"));
    lobbyPostDTO.setRounds(3);
    lobbyPostDTO.setRoundTimer(60);
    lobbyPostDTO.setClueTimer(10);
    lobbyPostDTO.setDiscussionTimer(60);

    // mapping
    Lobby lobby = LobbyDTOMapper.INSTANCE.convertLobbyPostDTOtoEntity(lobbyPostDTO);

    // check content
    assertEquals(lobbyPostDTO.getLobbyAdmin(), lobby.getHost().getUserId());
    assertEquals(lobbyPostDTO.getName(), lobby.getName());
    assertEquals(lobbyPostDTO.getPassword(), lobby.getPassword());
    assertEquals(lobbyPostDTO.getPlayerLimit(), lobby.getPlayerLimit());
    assertEquals(lobbyPostDTO.getRounds(), lobby.getRounds());
    assertEquals(lobbyPostDTO.getRoundTimer(), lobby.getRoundTimer());
    assertEquals(lobbyPostDTO.getClueTimer(), lobby.getClueTimer());
    assertEquals(lobbyPostDTO.getDiscussionTimer(), lobby.getDiscussionTimer());
  }

  @Test
  public void getLobby_fromLobby_toLobbyGetDTO_success() {
    // given
    Player hostPlayer = new Player();
    hostPlayer.setUserId(1L);
    hostPlayer.setUsername("hostPlayer");

    Theme theme = new Theme();
    theme.setName("Theme");

    Lobby testLobby = new Lobby();
    testLobby.setId(1L);
    testLobby.setName("lobbyName");
    testLobby.setPassword("password");
    testLobby.setPlayerLimit(3);
    testLobby.setRounds(3);
    testLobby.setRoundTimer(60);
    testLobby.setClueTimer(10);
    testLobby.setDiscussionTimer(60);
    testLobby.setHost(hostPlayer);
    testLobby.addPlayer(hostPlayer);
    testLobby.setThemes(List.of(theme));

    // mapping
    LobbyGetDTO lobbyGetDTO = LobbyDTOMapper.INSTANCE.convertEntityToLobbyGetDTO(testLobby);

    // check content
    assertEquals(testLobby.getId(), lobbyGetDTO.getId());
    assertEquals(testLobby.getName(), lobbyGetDTO.getName());
    assertEquals(testLobby.getHost().getUsername(), lobbyGetDTO.getLobbyAdmin());
    assertEquals(testLobby.getPlayers().get(0).getUsername(), lobbyGetDTO.getPlayers().get(0));
    assertEquals(testLobby.getPlayerLimit(), lobbyGetDTO.getPlayerLimit());
    assertEquals(testLobby.getPlayerCount(), lobbyGetDTO.getPlayerCount());
    assertEquals(testLobby.getThemes().get(0).getName(), lobbyGetDTO.getThemes().get(0));
    assertEquals(testLobby.getRounds(), lobbyGetDTO.getRounds());
    assertEquals(testLobby.getRoundTimer(), lobbyGetDTO.getRoundTimer());
    assertEquals(testLobby.getClueTimer(), lobbyGetDTO.getClueTimer());
    assertEquals(testLobby.getDiscussionTimer(), lobbyGetDTO.getDiscussionTimer());
  }

}
