package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.LobbyStatus;
import ch.uzh.ifi.hase.soprafs24.constant.LobbyType;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ThemeRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TimerServiceTest {

  @Mock
  private TaskScheduler scheduler;
  @Mock
  private SimpMessagingTemplate messagingTemplate;
  @Mock
  private ScheduledFuture<?> scheduledFuture;

  @InjectMocks
  @Spy
  private TimerService timerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void startAndStopTimer_notifyRemainingTime() {
    // given
    Long lobbyId = 1L;
    String timerType = "timerType";
    Integer duration = 5;
    Runnable onTimerEnd = mock(Runnable.class);
    doReturn(scheduledFuture).when(scheduler).scheduleAtFixedRate(any(Runnable.class), eq(1000L));

    // when
    timerService.startTimer(lobbyId, timerType, duration, onTimerEnd);

    // then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler).scheduleAtFixedRate(runnableCaptor.capture(), eq(1000L));

    // Simulate time decrement
    runnableCaptor.getValue().run();
    verify(messagingTemplate).convertAndSend(eq("/topic/" + lobbyId + "/" + timerType + "Timer"), anyInt());

    // Test stopping the timer
    timerService.stopTimer(lobbyId, timerType);
    verify(scheduledFuture).cancel(false);
  }

  @Test
  void startAndStopTimer_onEndTimer() {
    // given
    Long lobbyId = 1L;
    String timerType = "timerType";
    Integer duration = 0;
    Runnable onTimerEnd = mock(Runnable.class);
    doReturn(scheduledFuture).when(scheduler).scheduleAtFixedRate(any(Runnable.class), eq(1000L));

    // when
    timerService.startTimer(lobbyId, timerType, duration, onTimerEnd);

    // then
    ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler).scheduleAtFixedRate(runnableCaptor.capture(), eq(1000L));

    // Simulate time decrement
    runnableCaptor.getValue().run();
    runnableCaptor.getValue().run();

    // then
    verify(onTimerEnd).run();
  }

  @Test
  public void startRoundTimer_success() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setRoundTimer(3);
    Runnable onTimerEnd = mock(Runnable.class);
    doNothing().when(timerService).startTimer(anyLong(), anyString(), anyInt(), any(Runnable.class));

    // when
    timerService.startRoundTimer(game, onTimerEnd);

    // then
    verify(timerService).startTimer(eq(game.getLobbyId()), eq("round"), eq(game.getRoundTimer()), eq(onTimerEnd));
  }

  @Test
  public void startClueTimer_success() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setClueTimer(3);
    Runnable onTimerEnd = mock(Runnable.class);
    doNothing().when(timerService).startTimer(anyLong(), anyString(), anyInt(), any(Runnable.class));

    // when
    timerService.startClueTimer(game, onTimerEnd);

    // then
    verify(timerService).startTimer(eq(game.getLobbyId()), eq("clue"), eq(game.getClueTimer()), eq(onTimerEnd));
  }

  @Test
  public void startDiscussionTimer_success() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    game.setDiscussionTimer(3);
    Runnable onTimerEnd = mock(Runnable.class);
    doNothing().when(timerService).startTimer(anyLong(), anyString(), anyInt(), any(Runnable.class));

    // when
    timerService.startDiscussionTimer(game, onTimerEnd);

    // then
    verify(timerService).startTimer(eq(game.getLobbyId()), eq("discussion"), eq(game.getDiscussionTimer()), eq(onTimerEnd));
  }

  @Test
  public void startIntervalTimer_success() {
    // given
    Game game = new Game();
    game.setLobbyId(1L);
    Runnable onTimerEnd = mock(Runnable.class);
    doNothing().when(timerService).startTimer(anyLong(), anyString(), anyInt(), any(Runnable.class));

    // when
    timerService.startIntervalTimer(game, 5, onTimerEnd);

    // then
    verify(timerService).startTimer(eq(game.getLobbyId()), eq("interval"), eq(5), eq(onTimerEnd));
  }

}
