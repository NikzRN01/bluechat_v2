package com.bluechat.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a chat room in the application.
 * Uses concurrent collections for thread safety during user operations.
 */
@Data
public class ChatRoom {
    private final String id;
    private String name;
    
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<String> participants; // Set of usernames
    
    // Explicitly define getId to ensure it's available
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<String> getParticipants() {
        return participants;
    }
    
    /**
     * Creates a new chat room with the given name and generates a unique ID.
     */
    public ChatRoom(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.participants = ConcurrentHashMap.newKeySet();
    }
    
    /**
     * Adds a participant to the chat room.
     * 
     * @param username The username of the participant to add
     * @return true if the user was added, false if already present
     */
    public boolean addParticipant(String username) {
        return participants.add(username);
    }
    
    /**
     * Removes a participant from the chat room.
     * 
     * @param username The username of the participant to remove
     * @return true if the user was removed, false if not present
     */
    public boolean removeParticipant(String username) {
        return participants.remove(username);
    }
    
    /**
     * Checks if a user is in the chat room.
     * 
     * @param username The username to check
     * @return true if the user is in the room, false otherwise
     */
    public boolean hasParticipant(String username) {
        return participants.contains(username);
    }
    
    /**
     * Gets the number of participants in the room.
     * 
     * @return The count of participants
     */
    public int getParticipantCount() {
        return participants.size();
    }
}

