package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.Player;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.websocket.listener.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {
  private final SimpMessagingTemplate messagingTemplate;

  private final TranslationService translationService;

  private final UserService userService;

  private final LobbyService lobbyService;

  private SessionManager sessionManager;

  @Autowired
  public ChatService(SimpMessagingTemplate messagingTemplate, TranslationService translationService, UserService userService, LobbyService lobbyService, SessionManager sessionManager) {
    this.messagingTemplate = messagingTemplate;
    this.translationService = translationService;
    this.userService = userService;
    this.lobbyService = lobbyService;
    this.sessionManager = sessionManager;
  }

  public void sendTranslatedMessagesToUsersBase(Long lobbyId, ChatMessage chatMessage, String messageType) {
    // Broadcast message to each subscriber in their preferred language
    ConcurrentHashMap<String, Long> sessionMap = sessionManager.getSessionMap();
    for (Map.Entry<String, Long> entry : sessionMap.entrySet()) {
      String sessionId = entry.getKey();
      Long userId = entry.getValue();
      User user = userService.getUser(userId);
      if (checkRightlobby(lobbyId, userId)) {
        String user_language = user.getLanguage();
        String translatedMessage = translationService.translateText(chatMessage.getContent(), user_language);
        chatMessage.setContent(translatedMessage);
        User sender = userService.getUser(chatMessage.getUserId());
        chatMessage.setUsername(sender.getUsername());
        // messagingTemplate.convertAndSendToUser(sessionId, destination, chatMessage);
        String destination = "/queue/" + userId + "/" + messageType;
        messagingTemplate.convertAndSend(destination, chatMessage);
      }
    }
  }

  // for chat
  public void sendTranslatedMessagesToUsers(Long lobbyId, ChatMessage chatMessage) {
    sendTranslatedMessagesToUsersBase(lobbyId, chatMessage, "chat");
  }

  // for clue
  public void sendTranslatedClueMessagesToUsers(Long lobbyId, ChatMessage chatMessage) {
    sendTranslatedMessagesToUsersBase(lobbyId, chatMessage, "clue");
  }

  public void sendTranslatedMessagesToUser(Long userId, ChatMessage chatMessage) {
//        User user = userService.getUser(userId);
//        String translatedMessage = translationService.translateText(chatMessage.getContent(), user.getLanguage());
//        chatMessage.setContent(translatedMessage);
//        messagingTemplate.convertAndSendToUser(sessionId, destination, chatMessage);
    String destination = "/queue/" + userId + "/private";
    messagingTemplate.convertAndSend(destination, chatMessage);
  }

  public boolean checkRightlobby(Long lobbyId, Long userId) {
    Lobby current = lobbyService.getLobbyByUserId(userId);
    return lobbyId.equals(current.getId());
  }

  // purely for testing purposes
  public SessionManager getSessionManager() {
      return sessionManager;
  }

  public void setSessionManager(SessionManager sessionManager) {
      this.sessionManager = sessionManager;
  }
}
