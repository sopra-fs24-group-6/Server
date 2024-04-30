package ch.uzh.ifi.hase.soprafs24.websocket.listener;

import ch.uzh.ifi.hase.soprafs24.service.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketEventListener {

  private final AtomicInteger connectedClients = new AtomicInteger(0);
  private final SessionManager sessionManager;
  private final LobbyService lobbyService;

  @Autowired
  public WebSocketEventListener(LobbyService lobbyService, SessionManager sessionManager) {
    this.lobbyService = lobbyService;
    this.sessionManager = sessionManager;
  }

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
      StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
      String sessionId = headerAccessor.getSessionId();
      // Extract userId from headers, assuming it's sent under the key 'userId'
      String userIdString = headerAccessor.getFirstNativeHeader("userId");
      if (userIdString != null && !userIdString.isEmpty()) {
          Long userId = Long.parseLong(userIdString);
          sessionManager.addSession(sessionId, userId);
      } else {
          System.out.println("UserId is missing in the WebSocket connection headers");
      }
    int currentCount = connectedClients.incrementAndGet();
    System.out.println("Received a new web socket connection. Total connections: " + currentCount);
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
      StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
      String sessionId = headerAccessor.getSessionId();
      Long userId = sessionManager.removeSession(sessionId);
      int currentCount = connectedClients.decrementAndGet();
    System.out.println("A web socket connection was closed. Total connections: " + currentCount);

    // leave game
    lobbyService.leaveLobby(userId);
  }
}
