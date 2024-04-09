package ch.uzh.ifi.hase.soprafs24.websocket.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class WebSocketEventListener {

  private final AtomicInteger connectedClients = new AtomicInteger(0);

  @EventListener
  public void handleWebSocketConnectListener(SessionConnectEvent event) {
    int currentCount = connectedClients.incrementAndGet();
    System.out.println("Received a new web socket connection. Total connections: " + currentCount);
  }

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    int currentCount = connectedClients.decrementAndGet();
    System.out.println("A web socket connection was closed. Total connections: " + currentCount);
  }
}
