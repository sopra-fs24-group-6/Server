package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TimerService {

  private final Map<Long, Map<String, ScheduledFuture<?>>> gameTimers = new ConcurrentHashMap<>();
  private final TaskScheduler scheduler;
  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public TimerService(TaskScheduler scheduler, SimpMessagingTemplate messagingTemplate) {
    this.scheduler = scheduler;
    this.messagingTemplate = messagingTemplate;
  }

  public void startRoundTimer(Game game, Runnable onTimerEnd) {
    Long lobbyId = game.getLobbyId();
    Integer duration = game.getRoundTimer();
    startTimer(lobbyId, "round", duration, onTimerEnd);
  }

  public void startClueTimer(Game game, Runnable onTimerEnd) {
    Long lobbyId = game.getLobbyId();
    Integer duration = game.getClueTimer();
    startTimer(lobbyId, "clue", duration, onTimerEnd);
  }

  public void startDiscussionTimer(Game game, Runnable onTimerEnd) {
    Long lobbyId = game.getLobbyId();
    Integer duration = game.getDiscussionTimer();
    startTimer(lobbyId, "discussion", duration, onTimerEnd);
  }

  public void startIntervalTimer(Game game, Runnable onTimerEnd) {
    Long lobbyId = game.getLobbyId();
    Integer duration = 3;
    startTimer(lobbyId, "interval", duration, onTimerEnd);
  }

  public void startTimer(Long lobbyId, String timerType, Integer duration, Runnable onTimerEnd) {
    stopTimer(lobbyId, timerType);

    AtomicInteger remainingTime = new AtomicInteger(duration);
    ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
      int currentTime = remainingTime.getAndDecrement();
      if (currentTime >= 0) {
        messagingTemplate.convertAndSend(String.format("/topic/" + lobbyId + "/" + timerType + "Timer"), currentTime);
      } else {
        stopTimer(lobbyId, timerType);
        if (onTimerEnd != null) {
          onTimerEnd.run();
        }
      }
    }, 1000);

    Map<String, ScheduledFuture<?>> timerMap = gameTimers.computeIfAbsent(lobbyId, k -> new ConcurrentHashMap<>());
    timerMap.put(timerType, future);
  }

  public void stopTimer(Long lobbyId, String timerType) {
    Map<String, ScheduledFuture<?>> timers = gameTimers.get(lobbyId);
    if (timers != null) {
      ScheduledFuture<?> future = timers.remove(timerType);
      if (future != null) {
        future.cancel(false);
      }
    }
  }
}