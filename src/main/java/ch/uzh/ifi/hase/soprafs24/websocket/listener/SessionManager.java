package ch.uzh.ifi.hase.soprafs24.websocket.listener;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {
    private final ConcurrentHashMap<String, Long> sessionMap = new ConcurrentHashMap<>();

    
    public void addSession(String sessionId, Long userId) {
        sessionMap.put(sessionId, userId);
    }

    // return userId
    public Long removeSession(String sessionId) {
        return sessionMap.remove(sessionId);
    }

    public ConcurrentHashMap<String, Long> getSessionMap() {
        return sessionMap;
    }
}