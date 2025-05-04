package com.bluechat.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service class for managing users and authentication.
 * Provides methods for retrieving current user information and tracking active sessions.
 */
@Service
public class UserService {
    
    // Track active user sessions (username -> set of session IDs)
    private final Map<String, Set<String>> activeUserSessions = new ConcurrentHashMap<>();
    
    /**
     * Gets the currently authenticated username.
     * 
     * @return the username of the authenticated user, or "anonymous" if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        
        return principal.toString();
    }
    
    /**
     * Checks if the current user has the given role.
     * 
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }
    
    /**
     * Registers a user session.
     * 
     * @param username  the username
     * @param sessionId the session ID
     */
    public void registerUserSession(String username, String sessionId) {
        activeUserSessions.computeIfAbsent(username, k -> new HashSet<>())
                         .add(sessionId);
    }
    
    /**
     * Removes a user session.
     * 
     * @param username  the username
     * @param sessionId the session ID
     */
    public void removeUserSession(String username, String sessionId) {
        Set<String> sessions = activeUserSessions.get(username);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                activeUserSessions.remove(username);
            }
        }
    }
    
    /**
     * Checks if a user is active (has at least one session).
     * 
     * @param username the username to check
     * @return true if the user is active, false otherwise
     */
    public boolean isUserActive(String username) {
        Set<String> sessions = activeUserSessions.get(username);
        return sessions != null && !sessions.isEmpty();
    }
    
    /**
     * Gets all active users.
     * 
     * @return a set of active usernames
     */
    public Set<String> getActiveUsers() {
        return new HashSet<>(activeUserSessions.keySet());
    }
}

