package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.ChatService;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class ChatControllerTest {

  @Mock
  private ChatService chatService;

  @InjectMocks
  private ChatController chatController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  public void broadcastTranslatedMessage_success() {
    // given
    Long lobbyId = 1L;
    ChatMessage message = new ChatMessage();
    doNothing().when(chatService).sendTranslatedMessagesToUsers(anyLong(), any(ChatMessage.class));

    // when
    chatController.broadcastTranslatedMessage(lobbyId, message);

    // then
    verify(chatService).sendTranslatedMessagesToUsers(lobbyId, message);
  }

  @Test
  public void broadcastTranslatedClueMessage_success() {
    // given
    Long lobbyId = 1L;
    ChatMessage message = new ChatMessage();
    doNothing().when(chatService).sendTranslatedClueMessagesToUsers(anyLong(), any(ChatMessage.class));

    // when
    chatController.broadcastTranslatedClueMessage(lobbyId, message);

    // then
    verify(chatService).sendTranslatedClueMessagesToUsers(lobbyId, message);
  }

}