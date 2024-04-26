package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {

  @Mock
  private WordPairRepository wordPairRepository;

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private GameService gameService;


  @BeforeEach
  public void setup() { MockitoAnnotations.openMocks(this); }


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

    // then
    verify(playerRepository, times(3)).save(any(Player.class));
  }
}
