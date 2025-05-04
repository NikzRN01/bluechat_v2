package com.bluechat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a message in the chat application.
 * Messages can be regular chat messages or system messages (join/leave).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    /**
     * Enum representing types of messages that can be sent in a chat.
     */
    public enum MessageType {
        CHAT,   // Regular chat message
        JOIN,   // User joined notification
        LEAVE   // User left notification
    }

    private String id;
    private String roomId;
    private String sender;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;
    
    // Explicitly add these methods to ensure they are available even if Lombok has issues
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Creates a new chat message with generated ID and current timestamp.
     *
     * @param roomId  The ID of the room the message belongs to
     * @param sender  The username of the message sender
     * @param content The content of the message
     * @param type    The type of message (CHAT, JOIN, LEAVE)
     */
    public ChatMessage(String roomId, String sender, String content, MessageType type) {
        this.id = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a join message.
     *
     * @param roomId The room ID
     * @param username The username joining
     * @return A new JOIN type message
     */
    public static ChatMessage createJoinMessage(String roomId, String username) {
        return new ChatMessage(roomId, username, username + " joined the room", MessageType.JOIN);
    }

    /**
     * Creates a leave message.
     *
     * @param roomId The room ID
     * @param username The username leaving
     * @return A new LEAVE type message
     */
    public static ChatMessage createLeaveMessage(String roomId, String username) {
        return new ChatMessage(roomId, username, username + " left the room", MessageType.LEAVE);
    }
}

