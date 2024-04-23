package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Role;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Service
@Transactional
public class GameService {
    private final SimpMessagingTemplate messagingTemplate;
    private final TimerService timerService;
    private final TranslationService translationService;
    private final LobbyRepository lobbyRepository;
    private final WordPairRepository wordPairRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(SimpMessagingTemplate messagingTemplate,
                       TimerService timerService,
                       TranslationService translationService,
                       @Qualifier("lobbyRepository")LobbyRepository lobbyRepository,
                       @Qualifier("wordPairRepository")WordPairRepository wordPairRepository,
                       @Qualifier("playerRepository")PlayerRepository playerRepository) {
        this.messagingTemplate = messagingTemplate;
        this.timerService = timerService;
        this.translationService = translationService;
        this.lobbyRepository = lobbyRepository;
        this.wordPairRepository = wordPairRepository;
        this.playerRepository = playerRepository;
    }

  public void startGame(Long lobbyId, Long userId) {

    // initialize game
    Game game = initializeGame(lobbyId, userId);

    // notify all players that the game has been started
    notifyGameEvents(game, "startGame");

    // interval -> start round
    // TODO: implement multiple rounds
    timerService.startIntervalTimer(game, () -> startRound(game));
  }

  public Game initializeGame(Long lobbyId, Long userId) {
    // find lobby by lobbyId and verify host's userId
    Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(() -> new ResponseStatusException(
      HttpStatus.NOT_FOUND, "Lobby with id " + lobbyId + " could not be found."));
    if (userId.equals(lobby.getHost().getUserId())) {
      // initialize game (set theme, duration, players, etc. to game instance)
      Game game = new Game();
      game.setLobbyId(lobbyId);
      game.setRoundTimer(lobby.getRoundTimer());
      game.setClueTimer(lobby.getClueTimer());
      game.setDiscussionTimer(lobby.getDiscussionTimer());
      game.setPlayers(lobby.getPlayers());
      game.setThemeNames(lobby.getThemeNames());
      System.out.println(game);
      System.out.println(lobby);
      return game;
    } else {
      // Optionally, you could log this attempt, throw an exception, or return null
      System.out.println("User is not the host of the lobby. No action taken.");
      return null; // Indicates no game was initialized due to the condition not being met
    }
  }

  public void notifyGameEvents(Game game, String eventType) {
    EventNotification eventNotification = new EventNotification();
    eventNotification.setEventType(eventType);
    messagingTemplate.convertAndSend("/topic/" + game.getLobbyId() + "/gameEvents", eventNotification);
  }

  public void startRound(Game game) {
    // notification
    notifyGameEvents(game, "clue");
    // interval -> start assign phase
    // timerService.startIntervalTimer(game, () -> startAssignPhase(game));
    startAssignPhase(game);
  }

  public void startAssignPhase(Game game) {
    // assign words and roles, and notify each player
    assignWordsAndRoles(game);
    // interval -> start clue phase
    timerService.startIntervalTimer(game, () -> startCluePhase(game));
  }

  public void startCluePhase(Game game) {
    // shuffle players order
    List<Long> shuffledPlayerIds = shufflePlayersOrder(game);

    // start round timer -> when finished, then start discussion phase
    timerService.startRoundTimer(game, () -> startDiscussionPhase(game));

    // start clue turn, loop until roundTimer ends
    game.setIsCluePhase(true);
    Integer currentIndex = 0;
    startClueTurn(game, shuffledPlayerIds, currentIndex);
  }

  private List<Long> shufflePlayersOrder(Game game) {
    List<Player> players = game.getPlayers();
    List<Long> shuffledPlayerIds = new ArrayList<>();
    for (Player player : players) {
      shuffledPlayerIds.add(player.getUserId());
    }
    Collections.shuffle(shuffledPlayerIds);
    System.out.println(shuffledPlayerIds);
    return shuffledPlayerIds;
  }

    public void assignWordsAndRoles(Game game) {
        List<String> gameThemes = game.getThemeNames();
        for(String theme : gameThemes) {
            System.out.println(theme);
        }
        Random random = new Random();
        String randomTheme = gameThemes.stream()
                .skip(random.nextInt(gameThemes.size())) // Skip a random number of elements
                .findFirst() // This always succeeds unless the list is empty
                .orElseThrow(() -> new NoSuchElementException("No themes available in the lobby")); // Throw if the list is empty
        // Done: assign word randomly
        List<WordPair> wordPairList = wordPairRepository.findByTheme_Name(randomTheme);
        WordPair randomWordPair = wordPairList.stream()
                .skip(random.nextInt(wordPairList.size())) // Skip a random number of elements
                .findFirst() // This always succeeds unless the list is empty
                .orElseThrow(() -> new NoSuchElementException("No word pairs available in the theme")); // Throw if the list is empty
        boolean assignFirstAsWolf = random.nextBoolean();
        String wolf_word = assignFirstAsWolf ? randomWordPair.getFirstWord() : randomWordPair.getSecondWord();
        String villager_word = assignFirstAsWolf ? randomWordPair.getSecondWord() : randomWordPair.getFirstWord();
        // Done: notify assigned word to each player "/queue/{userId}/wordAssignment"
        List<Player> players = game.getPlayers();
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
                //For cost concern, this function is commented
                String Translated_text = translationService.translateText("your assigned word is: " + villager_word, villager.getLanguage());
                villagerNotification.setWord(Translated_text);
//                villagerNotification.setWord(villager_word);
                String destination = "/queue/" + villager.getUserId() + "/wordAssignment";
                messagingTemplate.convertAndSend(destination, villagerNotification);
            } else {
                Player wolf = players.get(i);
                wolf.setRole(Role.WOLF);
                wolf.setWord(wolf_word);
                Player updatedPlayer = playerRepository.save(wolf);
                playerRepository.flush();
                WordNotification wolfNotification = new WordNotification();
                //For cost concern, this function is commented
                String wolfanouncement = translationService.translateText("Your role is wolf.",wolf.getLanguage());
                wolfNotification.setWord(wolfanouncement);
//                wolfNotification.setWord("You are the wolf");
                String destination = "/queue/" + wolf.getUserId() + "/wordAssignment";
                messagingTemplate.convertAndSend(destination, wolfNotification);
            }
        }
    }

  private void startClueTurn(Game game, List<Long> playerIds, Integer currentIndex) {
    Long currentPlayerId = playerIds.get(currentIndex);
    TurnNotification turnNotification = new TurnNotification();
    turnNotification.setUserId(currentPlayerId);
    messagingTemplate.convertAndSend("/topic/" + game.getLobbyId() + "/clueTurn", turnNotification);
    timerService.startClueTimer(game, () -> onEndClueTimer(game, playerIds, currentIndex));
  }

  public void onEndClueTimer(Game game, List<Long> playerIds, Integer currentIndex) {
    if (game.getIsCluePhase()) {
      Integer nextIndex = (currentIndex + 1) % playerIds.size();
      startClueTurn(game, playerIds, nextIndex);
    }
  }

  public void startDiscussionPhase(Game game) {
    // stop clue timer
    game.setIsCluePhase(false);
    timerService.stopTimer(game.getLobbyId(), "clue");
    // start discussion phase
    notifyGameEvents(game, "discussion");
    timerService.startDiscussionTimer(game, () -> startVotingPhase(game));
  }

  public void startVotingPhase(Game game) {
    notifyGameEvents(game, "vote");
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

  public void endGame(Game game) {
    Lobby lobby = lobbyRepository.findById(game.getLobbyId()).orElseThrow(() -> new ResponseStatusException( HttpStatus.NOT_FOUND, "Lobby with id " + game.getLobbyId() + " could not be found."));

    List<Player> players = lobby.getPlayers();

    for (Player player : players) {
// using ID getter of player
      playerRepository.deleteById(player.getId());
    }
    /**
     * remove player from Lobby table
     * https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html#deleteById(ID)
     */
    lobbyRepository.delete(lobby);

    // Maybe new event in rabit Queue?
    notifyGameEvents(game, "EndGame");
  }

}
