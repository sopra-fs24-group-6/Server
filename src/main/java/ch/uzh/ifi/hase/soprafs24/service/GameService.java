package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WordPairRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.EventNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ResultNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TurnNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Service
public class GameService {

    private final SimpMessagingTemplate messagingTemplate;
    private final TimerService timerService;

    private final LobbyRepository lobbyRepository;

    private final WordPairRepository wordPairRepository;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(SimpMessagingTemplate messagingTemplate,
                       TimerService timerService,
                       @Qualifier("lobbyRepository")LobbyRepository lobbyRepository,
                       @Qualifier("wordPairRepository")WordPairRepository wordPairRepository,
                       @Qualifier("playerRepository")PlayerRepository playerRepository) {
        this.messagingTemplate = messagingTemplate;
        this.timerService = timerService;
        this.lobbyRepository = lobbyRepository;
        this.wordPairRepository = wordPairRepository;
        this.playerRepository = playerRepository;
    }

  public void startGame(Long lobbyId, Long userId) {

    // initialize game
    Game game = initializeGame(lobbyId, userId);

    // notify all players that the game has been started
    notifyGameEvents(game, "startGame");

    // start round
    // TODO: implement multiple rounds
    startRound(game);
  }

  public Game initializeGame(Long lobbyId, Long userId) {
    // TODO: find lobby by lobbyId and verify host's userId
    // TODO: initialize game (set theme, duration, players, etc. to game instance)
    Game game =  new Game();
    game.setLobbyId(1L);
    game.setRoundTimer(16);
    game.setClueTimer(3);
    game.setDiscussionTimer(5);
    return game;
  }

  public void notifyGameEvents(Game game, String eventType) {
    EventNotification eventNotification = new EventNotification();
    eventNotification.setEventType(eventType);
    messagingTemplate.convertAndSend("/topic/" + game.getLobbyId() + "/gameEvents", eventNotification);
  }

  public void startRound(Game game) {
    /**
     * Round proceeds as follows:
     *  1. start round timer
     *  2. assign word to player
     *  3. shuffle players' order
     *  4. start clue turn for each player
     *  5. start discussion phase
     *  6. start voting phase (not implemented yet)
     *  7. calculate result of voting
     *  8. announce winner(s) and loser(s)
     */

    // start round timer
    notifyGameEvents(game, "startRound");
    timerService.startRoundTimer(game, () -> onEndRoundTimer(game));

    // assign words and roles
    assignWordsAndRoles(game);

    // start clue phase -> discussion phase -> results
    // phase transition is managed by timer
    List<Long> shuffledPlayerIds = new ArrayList<>(Arrays.asList(1L, 2L));
    startClueTurn(game, shuffledPlayerIds);
  }

    public void assignWordsAndRoles(Game game) {
        // TODO: fetch words with certain theme from database
        Lobby lobby = lobbyRepository.findById(game.getLobbyId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Lobby with id " + game.getLobbyId() + " could not be found."));
        List<Theme> gameThemes = lobby.getThemes();
        Random random = new Random();
        Theme randomTheme = gameThemes.stream()
                .skip(random.nextInt(gameThemes.size())) // Skip a random number of elements
                .findFirst() // This always succeeds unless the list is empty
                .orElseThrow(() -> new NoSuchElementException("No themes available in the lobby")); // Throw if the list is empty
        // TODO: assign word randomly
        List<WordPair> wordPairList = wordPairRepository.findByTheme_Id(randomTheme.getId());
        WordPair randomWordPair = wordPairList.stream()
                .skip(random.nextInt(wordPairList.size())) // Skip a random number of elements
                .findFirst() // This always succeeds unless the list is empty
                .orElseThrow(() -> new NoSuchElementException("No word pairs available in the theme")); // Throw if the list is empty
        boolean assignFirstAsWolf = random.nextBoolean();
        String wolf_word = assignFirstAsWolf ? randomWordPair.getFirstWord() : randomWordPair.getSecondWord();
        String villager_word = assignFirstAsWolf ? randomWordPair.getSecondWord() : randomWordPair.getFirstWord();
        // TODO: notify assigned word to each player "/queue/{userId}/wordAssignment"
        List<Player> players = lobby.getPlayers();
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("No players available");
        }
        // Select a random player to be the wolf
        int wolfIndex = random.nextInt(players.size());
        for (int i = 0; i < players.size(); i++) {
            if (i != wolfIndex) { // Skip the wolf
                Player villager = players.get(i);
                villager.setRole(Role.VILLAGER);
                villager.setWord(villager_word);
                Player updatedPlayer = playerRepository.save(villager);
                playerRepository.flush();
                WordNotification villagerNotification = new WordNotification();
                villagerNotification.setWord(villager_word);
                villagerNotification.setRole(Role.VILLAGER);
                String destination = "/queue/" + villager.getUserId() + "/wordAssignment";
                messagingTemplate.convertAndSend(destination, villagerNotification);
            } else {
                Player wolf = players.get(i);
                wolf.setRole(Role.WOLF);
                wolf.setWord(wolf_word);
                Player updatedPlayer = playerRepository.save(wolf);
                playerRepository.flush();
                WordNotification wolfNotification = new WordNotification();
                wolfNotification.setWord(wolf_word);
                wolfNotification.setRole(Role.VILLAGER);
                String destination = "/queue/" + wolf.getUserId() + "/wordAssignment";
                messagingTemplate.convertAndSend(destination, wolfNotification);
            }
        }
    }

  private void startClueTurn(Game game, List<Long> playerIds) {
    if (!playerIds.isEmpty()) {
      Long currentPlayerId = playerIds.remove(0);
      TurnNotification turnNotification = new TurnNotification();
      turnNotification.setUserId(currentPlayerId);
      messagingTemplate.convertAndSend("/topic/" + game.getLobbyId() + "/clueTurn", turnNotification);
      timerService.startClueTimer(game, () -> onEndClueTimer(game, playerIds));
    }
  }

  public void onEndClueTimer(Game game, List<Long> playerIds) {
    if (playerIds.isEmpty()) {
      notifyGameEvents(game, "startDiscussion");
      timerService.startDiscussionTimer(game, () -> onEndDiscussionTimer(game));
    } else {
      startClueTurn(game, playerIds);
    }
  }

  public void onEndDiscussionTimer(Game game) {
    notifyGameEvents(game, "StartVoting");
  }

  public void onEndRoundTimer(Game game) {
    notifyGameEvents(game, "EndRound");
    notifyResults(game);
  }

  public void notifyResults(Game game) {
    // TODO: calculate results and notify to players
    ResultNotification resultNotification = new ResultNotification();
    resultNotification.setWinnerRole("WOLF");
    List<Long> winners = new ArrayList<>(Arrays.asList(1L, 3L));
    List<Long> losers = new ArrayList<>(Arrays.asList(2L));
    resultNotification.setWinners(winners);
    resultNotification.setLosers(losers);
    messagingTemplate.convertAndSend("/topic/" + game.getLobbyId() + "/result", resultNotification);
  }

}
