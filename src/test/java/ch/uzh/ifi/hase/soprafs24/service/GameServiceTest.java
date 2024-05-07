package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.matcher.WordNotificationMatcher;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ResultNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private WordPairRepository wordPairRepository;

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Mock
  private TimerService timerService;

  @Mock
  private LobbyService lobbyService;

  @Mock
  private VoteService voteService;

  @InjectMocks
  @Spy
  private GameService gameService;


  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  public void assignWordsAndRoles_success() {
    // given: define test themes
    Theme theme1 = new Theme();
    theme1.setId(1L);
    theme1.setName("Theme1");
    Theme theme2 = new Theme();
    theme2.setName("Theme2");
    theme2.setId(2L);
    // given: define test wordPairs
    WordPair wordPair1 = new WordPair();
    wordPair1.setTheme(theme1);
    wordPair1.setFirstWord("Word1");
    wordPair1.setSecondWord("Word2");
    WordPair wordPair2 = new WordPair();
    wordPair2.setTheme(theme1);
    wordPair2.setFirstWord("Word3");
    wordPair2.setSecondWord("Word4");
    List<WordPair> wordPairList = new ArrayList<>();
    wordPairList.add(wordPair1);
    wordPairList.add(wordPair2);
    // given: define test players
    Player player1 = new Player();
    player1.setUserId(1L);
    player1.setUsername("user1");
    Player player2 = new Player();
    player2.setUserId(2L);
    player2.setUsername("user2");
    Player player3 = new Player();
    player3.setUserId(3L);
    player3.setUsername("user3");
    Player updatedPlayer1 = new Player();
    updatedPlayer1.setUserId(player1.getUserId());
    updatedPlayer1.setUsername(player1.getUsername());
    updatedPlayer1.setRole(Role.VILLAGER);
    updatedPlayer1.setWord(wordPair2.getSecondWord());
    Player updatedPlayer2 = new Player();
    updatedPlayer2.setUserId(player2.getUserId());
    updatedPlayer2.setUsername(player2.getUsername());
    updatedPlayer2.setRole(Role.WOLF);
    updatedPlayer2.setWord(null);
    Player updatedPlayer3 = new Player();
    updatedPlayer3.setUserId(player3.getUserId());
    updatedPlayer3.setUsername(player3.getUsername());
    updatedPlayer3.setRole(Role.VILLAGER);
    updatedPlayer3.setWord(wordPair2.getSecondWord());
    // given: define test game
    Game game = new Game();
    game.setThemeNames(List.of(theme1.getName(), theme2.getName()));
    game.setPlayers(List.of(player1, player2, player3));
    // given: mock repository methods
    when(wordPairRepository.findByTheme_Name(anyString())).thenReturn(wordPairList);
    when(playerRepository.save(any(Player.class)))
      .thenReturn(updatedPlayer2)
      .thenReturn(updatedPlayer1)
      .thenReturn(updatedPlayer3);
    // given: set fixed random seed
    Random fixedRandom = new Random(12345);
    gameService.setRandom(fixedRandom);

    // when
    gameService.assignWordsAndRoles(game);

    // then: save players the number of players times
    verify(playerRepository, times(3)).save(any(Player.class));
    // then: notify word to each player
    verify(messagingTemplate, times(1))
      .convertAndSend(
        eq("/queue/1/wordAssignment"),
        argThat(new WordNotificationMatcher(null)));
    verify(messagingTemplate, times(1))
      .convertAndSend(
        eq("/queue/2/wordAssignment"),
        argThat(new WordNotificationMatcher("Word4")));
    verify(messagingTemplate, times(1))
      .convertAndSend(
        eq("/queue/3/wordAssignment"),
        argThat(new WordNotificationMatcher("Word4")));
  }

  @Test
  public void notifyResult_success() {
    // given
    User user = new User();

    Player winnerPlayer = new Player();
    winnerPlayer.setUserId(1L);
    winnerPlayer.setUsername("player1");
    Player loserPlayer = new Player();
    loserPlayer.setUserId(2L);
    loserPlayer.setUsername("player2");

    Result result = new Result();
    result.setWinnerRole(Role.WOLF);
    result.setWinnerPlayers(List.of(winnerPlayer));
    result.setLoserPlayers(List.of(loserPlayer));

    Game game = new Game();
    game.setCurrentRound(1);
    game.setRounds(1);
    gameService.putActiveGame(1L, game);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    doNothing().when(timerService).startIntervalTimer(any(Game.class), anyInt(), any());

    // when
    gameService.notifyResults(1L, result);

    // then
    verify(userRepository, times(2)).findById(anyLong());
    verify(userRepository, times(2)).save(any(User.class));
    verify(messagingTemplate, times(1))
      .convertAndSend(eq("/topic/1/result"),any(ResultNotification.class));
  }

  @Test
  public void endRound_underMaxRound_thenStartNewRound() {
    // given
    Game game = new Game();
    game.setCurrentRound(1);
    game.setRounds(3);
    doNothing().when(gameService).startRound(any(Game.class));

    // when
    gameService.endRound(game);

    // then
    assertEquals(2, game.getCurrentRound());
    verify(gameService, times(1)).startRound(eq(game));
  }

  @Test
  public void endRound_reachMaxRound_thenEndGame() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setCurrentRound(3);
    game.setRounds(3);
    gameService.putActiveGame(1L, game);

    Lobby lobby = new Lobby();
    when(lobbyService.getLobbyById(anyLong())).thenReturn(lobby);
    doNothing().when(lobbyService).deleteLobby(any(Lobby.class));
    doNothing().when(voteService).deleteVotesByLobbyId(anyLong());

    // when
    gameService.endRound(game);

    // then
    assertEquals(3, game.getCurrentRound());
    verify(lobbyService, times(1)).getLobbyById(1L);
    verify(lobbyService, times(1)).deleteLobby(lobby);
    assertNull(gameService.getActiveGameByLobbyId(1L), "The game in activeGames should be null.");
  }
}
