package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.Theme;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ThemeRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LobbyServiceTest {

  @Mock
  private LobbyRepository lobbyRepository;

  @Mock
  private ThemeRepository themeRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private LobbyService lobbyService;

  private Lobby testLobby;
  private Theme testTheme;
  private User hostUser;
  private Player hostPlayer;

  @BeforeEach
  public void setup() {
    testTheme = new Theme();
    testTheme.setId(1L);
    testTheme.setName("Theme");

    hostUser = new User();
    hostUser.setId(1L);
    hostUser.setUsername("hostUsername");
    hostPlayer = new Player();
    hostPlayer.setUserId(hostUser.getId());
    hostPlayer.setUsername(hostUser.getUsername());
    hostPlayer.setHost(true);

    testLobby = new Lobby();
    testLobby.setId(1L);
    testLobby.setName("lobbyName");
    testLobby.setPassword("password");
    testLobby.setHost(hostPlayer);
    testLobby.setType(LobbyType.PRIVATE);
    testLobby.setPlayers(new ArrayList<>(List.of(hostPlayer)));
    testLobby.setPlayerLimit(3);
    testLobby.setPlayerCount(1);
    testLobby.setRounds(3);
    testLobby.setRoundTimer(60);
    testLobby.setClueTimer(10);
    testLobby.setDiscussionTimer(30);
    testLobby.setStatus(LobbyStatus.OPEN);
    testLobby.setThemes(new ArrayList<>(List.of(testTheme)));
    hostPlayer.setLobby(testLobby);

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getAllLobbies_success() {
    // given
    List<Lobby> lobbyList = List.of(testLobby);
    Mockito.when(lobbyRepository.findAll()).thenReturn(lobbyList);

    // when
    List<Lobby> result = lobbyService.getAllLobbies();

    // then
    assertEquals(1, result.size());
    assertEquals(lobbyList, result);
  }

  @Test
  public void getLobby_validLobbyId_success() {
    // given
    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));

    // when
    Lobby result = lobbyService.getLobbyById(testLobby.getId());

    // then
    assertEquals(result.getId(), testLobby.getId());
    assertEquals(result.getName(), testLobby.getName());
  }

  @Test
  public void getLobby_invalidLobbyId_throwsException() {
    // given
    Long lobbyId = 2L;
    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.getLobbyById(lobbyId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void getLobbies_withoutParameters() {
    // given
    List<Lobby> lobbyList = List.of(testLobby);
    Mockito.when(lobbyRepository.findAll()).thenReturn(lobbyList);

    // when
    List<Lobby> result = lobbyService.getLobbies(null, null);

    // then
    assertEquals(1, result.size());
    assertEquals(lobbyList, result);
  }

  @Test
  public void getLobbies_validUsername() {
    // given
    List<Lobby> lobbyList = List.of(testLobby);
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(hostPlayer));

    // when
    List<Lobby> result = lobbyService.getLobbies("hostUsername", null);

    // then
    assertEquals(1, result.size());
    assertEquals(lobbyList, result);
  }

  @Test
  public void getLobbies_validUsernameAndNotJoinedLobby() {
    // given
    Player player = new Player();
    Mockito.when(playerRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(player));

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.getLobbies("username", null));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void getLobbies_validUserId() {
    // given
    List<Lobby> lobbyList = List.of(testLobby);
    Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(Optional.of(hostPlayer));

    // when
    List<Lobby> result = lobbyService.getLobbies(null, 1L);

    // then
    assertEquals(1, result.size());
    assertEquals(lobbyList, result);
  }

  @Test
  public void getLobbies_validUserIdAndNotJoinedLobby() {
    // given
    Player player = new Player();
    Mockito.when(playerRepository.findById(Mockito.any())).thenReturn(Optional.of(player));

    // when/ then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.getLobbies(null, 2L));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void getPlayers_validLobbyId() {
    // given
    List<Player> playerList = List.of(hostPlayer);
    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));

    // when
    List<Player> result = lobbyService.getPlayersById(1L);

    // then
    assertEquals(1, result.size());
    assertEquals(hostPlayer, result.get(0));
  }

  @Test
  public void sendPlayerListToLobby_success() {
    // given
    List<PlayerDTO> playerDTOS = new ArrayList<PlayerDTO>();

    // when
    lobbyService.sendPlayerListToLobby(playerDTOS, 1L);

    // then
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/lobbies/1/players"), eq(playerDTOS));
  }

  @Test
  public void sendLobbyInfoToLobby_success() {
    // given
    LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();

    // when
    lobbyService.sendLobbyInfoToLobby(1L, lobbyGetDTO);

    // then
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/lobbies/1/lobby_info"), eq(lobbyGetDTO));
  }

  @Test
  public void createPrivateLobby_validInputs_success() {
    // given
    Theme inputTheme = new Theme();
    inputTheme.setName(testTheme.getName());
    Player inputHost = new Player();
    inputHost.setUserId(hostPlayer.getUserId());

    Lobby newLobby = new Lobby();
    newLobby.setName("lobbyName");
    newLobby.setPassword("password");
    newLobby.setThemes(List.of(inputTheme));
    newLobby.setHost(inputHost);
    newLobby.setIsPrivate(true);

    Mockito.when(lobbyRepository.findByName(Mockito.any())).thenReturn(Optional.empty());
    Mockito.when(themeRepository.findByName(Mockito.any())).thenReturn(Optional.of(testTheme));
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(hostUser));
    Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(newLobby);

    // when
    Lobby createdLobby = lobbyService.createLobby(newLobby);

    // then
    Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(createdLobby.getId(), newLobby.getId());
    assertEquals(createdLobby.getName(), newLobby.getName());
    assertEquals(createdLobby.getPassword(), newLobby.getPassword());
    assertEquals(createdLobby.getStatus(), LobbyStatus.OPEN);
    assertEquals(createdLobby.getIsPrivate(), true);
    assertEquals(createdLobby.getPlayers().get(0).getUserId(), hostPlayer.getUserId());
    assertEquals(createdLobby.getPlayerCount(), 1);
  }

  @Test
  public void createLobby_invalidUserId_throwsException() {
    // given
    Player inputHost = new Player();
    inputHost.setUserId(2L);

    Lobby newLobby = new Lobby();
    newLobby.setHost(inputHost);

    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.createLobby(newLobby));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void createLobby_invalidLobbyName_throwsException() {
    // given
    Lobby newLobby = new Lobby();
    newLobby.setName("duplicateLobbyName");

    Mockito.when(lobbyRepository.findByName(Mockito.any())).thenReturn(Optional.of(newLobby));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.createLobby(newLobby));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void updateLobby_validInput_success() {
    // given
    Theme newTheme = new Theme();
    newTheme.setName("newTheme");

    Lobby newLobby = new Lobby();
    newLobby.setName("newLobbyName");
    newLobby.setPlayerLimit(5);
    newLobby.setThemes(List.of(newTheme));

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));
    Mockito.when(themeRepository.findByName(Mockito.any())).thenReturn(Optional.of(newTheme));

    // when
    lobbyService.updateLobby(testLobby.getId(), newLobby);

    // then
    Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
  }

  @Test
  public void updateLobby_invalidLobbyName_throwsException() {
    // given
    Lobby newLobby = new Lobby();
    newLobby.setName("LobbyName");

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.updateLobby(testLobby.getId(), newLobby));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
  }

  @Test
  public void updateLobby_invalidPlayerLimit_throwsException() {
    // given
    testLobby.setPlayerCount(6);
    Lobby newLobby = new Lobby();
    newLobby.setPlayerLimit(4);

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.updateLobby(testLobby.getId(), newLobby));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void addPlayer_validInput_success() {
    // given
    User newUser = new User();
    newUser.setId(2L);
    newUser.setUsername("newUsername");

    Player newPlayer = new Player();
    newPlayer.setUserId(newUser.getId());
    newPlayer.setUsername(newUser.getUsername());
    newPlayer.setHost(false);

    Lobby newLobby = new Lobby();
    newLobby.setId(1L);
    newLobby.setPlayers(List.of(hostPlayer, newPlayer));
    newLobby.setPlayerCount(2);

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(newUser));
    Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(newLobby);

    // when
    Lobby updatedLobby = lobbyService.addPlayerToLobby(testLobby.getId(), newUser.getId());

    // then
    Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(updatedLobby.getPlayerCount(), 2);
    assertEquals(updatedLobby.getPlayers().get(1).getUserId(), newPlayer.getUserId());
  }

  @Test
  public void addPlayer_whenLobbyIsFull_throwsException() {
    // given
    testLobby.setPlayerCount(testLobby.getPlayerLimit());

    User newUser = new User();
    newUser.setId(2L);
    newUser.setUsername("newUsername");

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(newUser));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.addPlayerToLobby(testLobby.getId(), newUser.getId()));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void addPlayer_AlreadyJoiningLobby_throwsException() {
    // given
    User newUser = new User();
    newUser.setId(2L);
    newUser.setUsername("newUsername");

    Player newPlayer = new Player();
    newPlayer.setUserId(newUser.getId());
    newPlayer.setUsername(newUser.getUsername());
    newUser.setPlayer(newPlayer);

    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));
    Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(newUser));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.addPlayerToLobby(testLobby.getId(), newUser.getId()));
    assertEquals(HttpStatus.CONFLICT, exception.getStatus());
  }

  @Test
  public void authenticateLobby_invalidPassword() {
    // given
    Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(testLobby));

    // when/then
    ResponseStatusException exception = assertThrows(
      ResponseStatusException.class, () -> lobbyService.authenticateLobby(1L, "wrongPassword"));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
  }

  @Test
  public void getAllThemes_success() {
    // given
    List<Theme> themeList = List.of(testTheme);
    Mockito.when(themeRepository.findAll()).thenReturn(themeList);

    // when
    List<String> result = lobbyService.getThemes();

    // then
    assertEquals(1, result.size());
    assertEquals(testTheme.getName(), result.get(0));
  }
}
