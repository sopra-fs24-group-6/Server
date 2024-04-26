package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.EventNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
public class GameServiceIntegrationTest {

  @Qualifier("lobbyRepository")
  @Autowired
  private LobbyRepository lobbyRepository;

  @Qualifier("playerRepository")
  @Autowired
  private PlayerRepository playerRepository;

  @Qualifier("themeRepository")
  @Autowired
  private ThemeRepository themeRepository;

  @Qualifier("wordPairRepository")
  @Autowired
  private WordPairRepository wordPairRepository;

  @MockBean
  private TimerService timerService;

  @MockBean
  private SimpMessagingTemplate messagingTemplate;

  @Autowired
  private GameService gameService;


  /**
   * define test players, theme, wordPair, lobby and save them to each repository
   */
  private Player player1;
  private Player player2;
  private Player player3;
  private Theme testTheme;
  private WordPair testWordPair;
  private Lobby testLobby;

  @BeforeEach
  public void setup() {
    playerRepository.deleteAll();
    lobbyRepository.deleteAll();
    wordPairRepository.deleteAll();

    player1 = new Player();
    player1.setUserId(1L);
    player1.setUsername("user1");
    player1.setHost(true);
    player2 = new Player();
    player2.setUserId(2L);
    player2.setUsername("user2");
    player2.setHost(false);
    player3 = new Player();
    player3.setUserId(3L);
    player3.setUsername("user3");
    player3.setHost(false);

    testTheme = new Theme();
    testTheme.setId(1L);
    testTheme.setName("Theme");
    themeRepository.save(testTheme);

    testWordPair = new WordPair();
    testWordPair.setId(1L);
    testWordPair.setFirstWord("word1");
    testWordPair.setSecondWord("word2");
    testWordPair.setTheme(testTheme);
    wordPairRepository.save(testWordPair);

    testLobby = new Lobby();
    testLobby.setName("lobbyName");
    testLobby.setType(LobbyType.PUBLIC);
    testLobby.setPlayerLimit(3);
    testLobby.setRounds(1);
    testLobby.setRoundTimer(2);
    testLobby.setClueTimer(1);
    testLobby.setDiscussionTimer(1);
    testLobby.setStatus(LobbyStatus.IN_PROGRESS);
    testLobby.setThemes(new ArrayList<>(List.of(testTheme)));
    testLobby.setHost(player1);
    testLobby.addPlayer(player1);
    lobbyRepository.save(testLobby);

    playerRepository.save(player2);
    playerRepository.save(player2);
    testLobby.addPlayer(player2);
    testLobby.addPlayer(player3);
    lobbyRepository.save(testLobby);

    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testWholeGameProcess_success() {
    /**
     * Run startGame and the following game process until clue phase.
     */
    // given: run callback when intervalTimer ends
    doAnswer(invocation -> {
      Runnable onTimerEnd = invocation.getArgument(2);
      if (onTimerEnd != null) {
        onTimerEnd.run();
      }
      return null;
    }).when(timerService).startIntervalTimer(any(Game.class), any(Integer.class), any(Runnable.class));
    // given: do nothing when startClueTimer and startRoundTimer end to handle procedure manually
    doNothing().when(timerService).startClueTimer(any(Game.class), any(Runnable.class));
    doNothing().when(timerService).startRoundTimer(any(Game.class), any(Runnable.class));

    // when: run startGame until clue phase
    gameService.startGame(testLobby.getId(), player1.getUserId());

    // then: check if word and role have been assigned to each player
    Optional<Player> foundPlayer1 = playerRepository.findById(player1.getUserId());
    Optional<Player> foundPlayer2 = playerRepository.findById(player2.getUserId());
    Optional<Player> foundPlayer3 = playerRepository.findById(player3.getUserId());
    assertTrue(foundPlayer1.isPresent(), "Player1 not found");
    assertTrue(foundPlayer2.isPresent(), "Player2 not found");
    assertTrue(foundPlayer3.isPresent(), "Player3 not found");
    assertNotNull(foundPlayer1.get().getRole(), "Role 1 is null");
    assertNotNull(foundPlayer2.get().getRole(), "Role 2 is null");
    assertNotNull(foundPlayer3.get().getRole(), "Role 3 is null");
    assertNotNull(foundPlayer1.get().getWord(), "Word 1 is null");
    assertNotNull(foundPlayer2.get().getWord(), "Word 2 is null");
    assertNotNull(foundPlayer3.get().getWord(), "Word 3 is null");
    // then: notify word assignment
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/queue/" + player1.getUserId() + "/wordAssignment"), any(WordNotification.class));
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/queue/" + player2.getUserId() + "/wordAssignment"), any(WordNotification.class));
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/queue/" + player3.getUserId() + "/wordAssignment"), any(WordNotification.class));

    /**
     * Run clue turn, and then, run discussion and vote phase.
     */
    // given: run callback when discussionTimer ends
    doAnswer(invocation -> {
      Runnable onTimerEnd = invocation.getArgument(1);
      if (onTimerEnd != null) {
        onTimerEnd.run();
      }
      return null;
    }).when(timerService).startDiscussionTimer(any(Game.class), any(Runnable.class));

    // when: run clue turn
    ArgumentCaptor<Runnable> clueTimerCallbackCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(timerService, times(1))
      .startClueTimer(any(Game.class), clueTimerCallbackCaptor.capture());
    Runnable clueTimerCallback = clueTimerCallbackCaptor.getValue();
    clueTimerCallback.run();
    // when: round timer ends -> run callback startDiscussionPhase -> run call back startVotingPhase
    ArgumentCaptor<Runnable> roundTimerCallbackCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(timerService, times(1))
      .startRoundTimer(any(Game.class), roundTimerCallbackCaptor.capture());
    Runnable roundTimerCallback = roundTimerCallbackCaptor.getValue();
    roundTimerCallback.run();

    // then: notify event of startGame, clue, discussion, vote
    verify(messagingTemplate, times(4))
      .convertAndSend(eq("/topic/" + testLobby.getId() + "/gameEvents"), any(EventNotification.class));
  }


  @Test
  public void testEndGame_success() {
    // given
    Game game = gameService.initializeGame(testLobby.getId(), player1.getUserId());

    // when
    gameService.endGame(game);

    // then: lobby and all players should be deleted
    Optional<Lobby> deletedLobby = lobbyRepository.findById(testLobby.getId());
    assertThat(deletedLobby.isEmpty()).isTrue();
    assertThat(playerRepository.findAll().isEmpty()).isTrue();
  }
}
