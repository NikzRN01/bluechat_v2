package com.bluechat.service;

import com.bluechat.model.ChatMessage;
import com.bluechat.model.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for managing chat rooms and messages.
 * Provides methods for room management and message broadcasting.
 */
@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Thread-safe map of chat rooms (roomId -> ChatRoom)
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        
        // Create a default public room
        createRoom("Public Chat");
    }

    /**
     * Creates a new chat room with the given name.
     * 
     * @param roomName the name of the room to create
     * @return the created ChatRoom
     */
    public ChatRoom createRoom(String roomName) {
        ChatRoom room = new ChatRoom(roomName);
        chatRooms.put(room.getId(), room);
        return room;
    }

    /**
     * Gets a chat room by its ID.
     * 
     * @param roomId the ID of the room to get
     * @return the ChatRoom or null if not found
     */
    public ChatRoom getRoomById(String roomId) {
        return chatRooms.get(roomId);
    }

    /**
     * Gets all available chat rooms.
     * 
     * @return a list of all chat rooms
     */
    public List<ChatRoom> getAllRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    /**
     * Adds a user to a chat room and broadcasts a join message.
     * 
     * @param roomId   the ID of the room to join
     * @param username the username of the user joining
     * @return true if the user was added, false if the room doesn't exist
     */
    public boolean addUserToRoom(String roomId, String username) {
        ChatRoom room = getRoomById(roomId);
        if (room == null) {
            return false;
        }
        
        boolean added = room.addParticipant(username);
        if (added) {
            // Broadcast join message to all users in the room
            ChatMessage joinMessage = ChatMessage.createJoinMessage(roomId, username);
            sendMessage(joinMessage);
        }
        
        return added;
    }

    /**
     * Removes a user from a chat room and broadcasts a leave message.
     * 
     * @param roomId   the ID of the room to leave
     * @param username the username of the user leaving
     * @return true if the user was removed, false if the room doesn't exist
     */
    public boolean removeUserFromRoom(String roomId, String username) {
        ChatRoom room = getRoomById(roomId);
        if (room == null) {
            return false;
        }
        
        boolean removed = room.removeParticipant(username);
        if (removed) {
            // Broadcast leave message to all users in the room
            ChatMessage leaveMessage = ChatMessage.createLeaveMessage(roomId, username);
            sendMessage(leaveMessage);
        }
        
        return removed;
    }

    /**
     * Sends a message to all users in a chat room.
     * 
     * @param message the message to send
     */
    public void sendMessage(ChatMessage message) {
        // Send to the specific room topic
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
    }
}

