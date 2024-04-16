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

  private final SimpMessagingTemplate template;

  private final ChatService chatService;

  public ChatController(SimpMessagingTemplate template, ChatService chatService)
  {
    this.template = template;
    this.chatService = chatService;
  }

  @MessageMapping("/chat/{lobbyId}/sendMessage")
  public void broadcastMessage(@DestinationVariable Long lobbyId, @Payload ChatMessage message) {
    // At this moment, just broadcast original message
    // TODO: translate original message using Google API in ChatService
    // TODO: broadcast original and translated content

    String destination = "/topic/" + lobbyId + "/chat";
    template.convertAndSend(destination, message);
  }

    @MessageMapping("/chat/{lobbyId}/sendMessage")
    public void broadcastTranslatedMessage(@DestinationVariable Long lobbyId, @Payload ChatMessage message) {
        // At this moment, just broadcast original message
        chatService.sendTranslatedMessagesToUsers(lobbyId, message);
        String destination = "/topic/" + lobbyId + "/chat";
        template.convertAndSend(destination, message);
    }
}