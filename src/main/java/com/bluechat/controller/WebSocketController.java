package com.bluechat.controller;

import com.bluechat.model.ChatMessage;
import com.bluechat.service.ChatService;
import com.bluechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Controller for handling WebSocket message exchanges.
 * Manages real-time message broadcasting in chat rooms.
 */
@Controller
public class WebSocketController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public WebSocketController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    /**
     * Handles messages sent to chat rooms.
     * Messages are broadcast to all subscribers of the room topic.
     *
     * @param roomId the ID of the room the message is for
     * @param message the chat message payload
     * @param principal the user principal (authenticated user)
     * @return the message to broadcast (same as input but with sender info)
     */
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessage message,
            Principal principal) {
        
        System.out.println("Received message: " + message.getContent() + " from " + message.getSender());
        
        // Set sender if not already set, handle null principal
        if (message.getSender() == null || message.getSender().isEmpty()) {
            if (principal != null) {
                message.setSender(principal.getName());
            } else {
                // This shouldn't happen in normal operation, but let's handle it gracefully
                message.setSender("Unknown");
                System.err.println("Warning: Both message sender and principal are null");
            }
        }
        
        // Ensure the roomId is set
        message.setRoomId(roomId);
        
        // Set message type to CHAT if not specified
        if (message.getType() == null) {
            message.setType(ChatMessage.MessageType.CHAT);
        }
        
        // Set timestamp
        if (message.getTimestamp() == null) {
            message.setTimestamp(java.time.LocalDateTime.now());
        }
        
        System.out.println("Broadcasting message to room: " + roomId);
        
        // Use the chat service to broadcast the message
        chatService.sendMessage(message);
    }

    /**
     * Handles user join events from WebSocket connections.
     * Stores user session information and notifies others about the join.
     *
     * @param roomId the ID of the room being joined
     * @param message the join message details
     * @param headerAccessor accessor for message headers to store session data
     * @param principal the user principal (authenticated user)
     */
    @MessageMapping("/chat.addUser/{roomId}")
    public void addUser(
            @DestinationVariable String roomId,
            @Payload ChatMessage message,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal) {
        
        String username;
        if (principal != null) {
            username = principal.getName();
        } else {
            // Fallback to sender from message if principal is null
            username = message.getSender();
            if (username == null || username.isEmpty()) {
                // Use a default username if all else fails
                username = "Anonymous";
            }
        }
        
        // Add username to web socket session
        headerAccessor.getSessionAttributes().put("username", username);
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        
        // Register session
        String sessionId = headerAccessor.getSessionId();
        userService.registerUserSession(username, sessionId);
        
        // Add user to room (the service will broadcast join message)
        chatService.addUserToRoom(roomId, username);
    }
}

