package ch.uzh.ifi.hase.soprafs24.service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.Lobby;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.websocket.dto.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.websocket.listener.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ChatServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private TranslationService translationService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyService lobbyService;

    @Mock
    private SessionManager sessionManager;

    @InjectMocks
    private ChatService chatService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendTranslatedMessagesToUsers() {
        // Arrange
        Long lobby1Id = 1L;
        Lobby lobby1 = new Lobby();
        lobby1.setId(lobby1Id);
        ChatMessage originalMessage = new ChatMessage();
        originalMessage.setContent("HelloWorld");
        originalMessage.setUserId(3L);
        SessionManager fakesessionManager = new SessionManager();
//        ConcurrentHashMap<String, Long> fakeSessionMap = new ConcurrentHashMap<>();
        fakesessionManager.addSession("session1", 1L);
        fakesessionManager.addSession("session2", 2L);
//        fakeSessionMap.put("session1", 1L);
//        fakeSessionMap.put("session2", 2L);


        when(sessionManager.getSessionMap()).thenReturn(fakesessionManager.getSessionMap());
        User user1 = new User();
        User user2 = new User();
        User sender = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setLanguage("en");
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setLanguage("zh");
        sender.setId(3L);
        sender.setUsername("user3");
        when(userService.getUser(1L)).thenReturn(user1);
        when(userService.getUser(2L)).thenReturn(user2);
        when(userService.getUser(3L)).thenReturn(sender);
        when(translationService.translateText("Hello World", "en")).thenReturn("Hello World Translated");
        when(translationService.translateText("Hello World", "zh")).thenReturn("你好，世界");
        when(lobbyService.getLobbyByUserId(user1.getId())).thenReturn(lobby1);
        when(lobbyService.getLobbyByUserId(user2.getId())).thenReturn(lobby1);

        // Act
        chatService.sendTranslatedMessagesToUsers(lobby1Id, originalMessage);

        // Assert
//        ChatMessage chatMessage1 = new ChatMessage();
//        ChatMessage chatMessage2 = new ChatMessage();
//
//        chatMessage1.setContent("Hello World Translated");
//        chatMessage2.setContent("你好，世界");
//        verify(messagingTemplate).convertAndSendToUser("session1", "/topic/1/chat", chatMessage1);
//        verify(messagingTemplate).convertAndSendToUser("session2", "/topic/1/chat", chatMessage2);
        verify(messagingTemplate).convertAndSend(eq("/queue/1/chat"), any(ChatMessage.class));
        verify(messagingTemplate).convertAndSend(eq("/queue/2/chat"), any(ChatMessage.class));
        verifyNoMoreInteractions(messagingTemplate);
    }
}