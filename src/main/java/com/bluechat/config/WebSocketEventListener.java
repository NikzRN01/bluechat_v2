package com.bluechat.config;

import com.bluechat.service.ChatService;
import com.bluechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public WebSocketEventListener(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String username = (String) headerAccessor.getUser().getName();
        
        // Log connection
        System.out.println("User connected: " + username + " (Session ID: " + sessionId + ")");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Get stored username and roomId
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        
        if (username != null && roomId != null) {
            System.out.println("User disconnected: " + username + " from room: " + roomId);
            
            // Remove user from room
            chatService.removeUserFromRoom(roomId, username);
            
            // Remove user session
            userService.removeUserSession(sessionId);
        }
    }
}

