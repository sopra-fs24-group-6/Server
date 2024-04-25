package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Autowired
  private TimerService timerService;

  @Autowired
  private GameService gameService;


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

  // TODO: test the whole game process
//  @Test
//  public void testStartGame_success() throws InterruptedException {
//    // when
//    gameService.startGame(testLobby.getId(), player1.getUserId());
//  }


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
