package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.EventNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
public class LobbyServiceIntegrationTest {

  @Qualifier("lobbyRepository")
  @Autowired
  private LobbyRepository lobbyRepository;

  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @MockBean
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private LobbyService lobbyService;


  private Long user1_id;
  private Long user2_id;
  private Long testlobby_id;

  @BeforeEach
  public void setup() {
    // clean repository
    List<Lobby> existingLobby = lobbyRepository.findAll();
    if (!existingLobby.isEmpty()) {
      lobbyService.deleteLobby(existingLobby.get(0));
    }
    userRepository.deleteAll();

    // create host
    User user1 = new User();
    user1.setUsername("user1");
    user1.setPassword("password");
    user1.setToken("token1");
    user1.setStatus(UserStatus.ONLINE);
    user1.setCreationDate(new Date());
    user1.setLanguage("en");
    user1 = userRepository.save(user1);
    user1_id = user1.getId();

    Player player1 = new Player();
    player1.setUserId(user1.getId());
    player1.setUsername(user1.getUsername());
    player1.setHost(true);
    user1.setPlayer(player1);

    // create lobby
    Lobby testLobby = new Lobby();
    testLobby.setName("lobbyName");
    testLobby.setType(LobbyType.PUBLIC);
    testLobby.setPlayerLimit(3);
    testLobby.setRounds(1);
    testLobby.setRoundTimer(2);
    testLobby.setClueTimer(1);
    testLobby.setDiscussionTimer(1);
    testLobby.setStatus(LobbyStatus.OPEN);
    testLobby.setHost(player1);
    testLobby.addPlayer(player1);
    testLobby = lobbyRepository.save(testLobby);
    testlobby_id = testLobby.getId();

    // add other players
    User user2 = new User();
    user2.setUsername("user2");
    user2.setPassword("password");
    user2.setToken("token2");
    user2.setStatus(UserStatus.ONLINE);
    user2.setCreationDate(new Date());
    user2.setLanguage("en");
    user2 = userRepository.save(user2);
    user2_id = user2.getId();

    Player player2 = new Player();
    player2.setUserId(user2.getId());
    player2.setUsername(user2.getUsername());
    player2.setHost(false);
    player2 = playerRepository.save(player2);
    user2.setPlayer(player2);

    testLobby.addPlayer(player2);
    testLobby = lobbyRepository.save(testLobby);

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void kickPlayerFromLobby_validInput() {
    // given
    Long lobbyId = testlobby_id;
    Long targetId = user2_id;
    Long requesterId = user1_id; // host

    // when
    Lobby updatedLobby = lobbyService.kickPlayerFromLobby(lobbyId, targetId, requesterId);

    // then
    assertEquals(1, updatedLobby.getPlayerCount());
    assertTrue(updatedLobby.getPlayers().stream().noneMatch(
      p -> p.getUserId().equals(targetId)), "Player " + targetId + " should be removed from the lobby.");
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/lobbies/" + lobbyId + "/lobby_event/" + targetId), any(EventNotification.class));
  }

  @Test
  public void kickPlayerFromLobby_NotByHost_throwException() {
    // given
    Long lobbyId = testlobby_id;
    Long targetId = user2_id;
    Long requesterId = user2_id; // not host

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.kickPlayerFromLobby(lobbyId, targetId, requesterId));
    System.out.println(exception.getMessage());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
  }

  @Test
  public void leaveLobby_NotHost_success() {
    // given
    Long lobbyId = testlobby_id;
    Long userId = user2_id;

    // when
    lobbyService.leaveLobby(userId);

    // then
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/lobbies/" + lobbyId + "/lobby_info"), any(LobbyGetDTO.class));
  }

  @Test
  public void leaveLobby_Host_success() {
    // given
    Long lobbyId = testlobby_id;
    Long userId = user1_id;

    // when
    lobbyService.leaveLobby(userId);

    // then
    Optional<Lobby> result = lobbyRepository.findById(lobbyId);
    assertTrue(result.isEmpty(), "Lobby should be empty (deleted)");
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/lobbies/" + lobbyId + "/lobby_event"), any(EventNotification.class));
  }

  @Test
  public void updateLobbyStatus_success() {
    // given
    Long lobbyId = testlobby_id;

    // when
    lobbyService.updateLobbyStatus(lobbyId, LobbyStatus.IN_PROGRESS);

    // then
    Optional<Lobby> result = lobbyRepository.findById(lobbyId);
    assertTrue(result.isPresent(), "Lobby should be empty (deleted)");
    assertEquals(LobbyStatus.IN_PROGRESS, result.get().getStatus());
  }



}
