package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {


  private final ChatService chatService;

  public ChatController(ChatService chatService)
  {
    this.chatService = chatService;
  }

    @MessageMapping("/chat/{lobbyId}/sendMessage")
    public void broadcastTranslatedMessage(@DestinationVariable Long lobbyId, @Payload ChatMessage message) {
        // translate message and send to each player
        chatService.sendTranslatedMessagesToUsers(lobbyId, message);
    }

  @MessageMapping("/clue/{lobbyId}/sendMessage")
  public void broadcastTranslatedClueMessage(@DestinationVariable Long lobbyId, @Payload ChatMessage message) {
    // translate message and send to each player
    chatService.sendTranslatedClueMessagesToUsers(lobbyId, message);
  }
}