package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.EventNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ResultNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.TurnNotification;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.WordNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class GameService {

  private final SimpMessagingTemplate messagingTemplate;
  private final TimerService timerService;

  @Autowired
  public GameService(SimpMessagingTemplate messagingTemplate, TimerService timerService) {
    this.messagingTemplate = messagingTemplate;
    this.timerService = timerService;
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
    // TODO: assign word randomly
    // TODO: notify assigned word to each player "/queue/{userId}/wordAssignment"
    WordNotification wordNotification = new WordNotification();
    wordNotification.setWord("dog");
    messagingTemplate.convertAndSend("/queue/1/wordAssignment", wordNotification);
    wordNotification.setWord(null);
    messagingTemplate.convertAndSend("/queue/2/wordAssignment", wordNotification);
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
