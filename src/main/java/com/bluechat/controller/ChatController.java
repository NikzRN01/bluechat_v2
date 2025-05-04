package com.bluechat.controller;

import com.bluechat.model.ChatRoom;
import com.bluechat.service.ChatService;
import com.bluechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling chat room related HTTP requests.
 * Manages room creation, listing, and user access to rooms.
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    /**
     * Displays the list of available chat rooms.
     *
     * @param model the Model to add attributes to
     * @return the view name
     */
    @GetMapping("/rooms")
    public String getRooms(Model model) {
        model.addAttribute("rooms", chatService.getAllRooms());
        model.addAttribute("username", userService.getCurrentUsername());
        return "rooms";
    }

    /**
     * Handles room creation requests.
     *
     * @param roomName the name of the room to create
     * @return redirect URL
     */
    @PostMapping("/rooms")
    public String createRoom(@RequestParam String roomName) {
        ChatRoom room = chatService.createRoom(roomName);
        return "redirect:/chat/rooms";
    }

    /**
     * Displays a specific chat room.
     *
     * @param roomId the ID of the room to display
     * @param model  the Model to add attributes to
     * @return the view name or a redirect if the room doesn't exist
     */
    @GetMapping("/room/{roomId}")
    public String getRoom(@PathVariable String roomId, Model model) {
        ChatRoom room = chatService.getRoomById(roomId);
        if (room == null) {
            return "redirect:/chat/rooms";
        }

        String username = userService.getCurrentUsername();
        // Add user to the room
        chatService.addUserToRoom(roomId, username);

        model.addAttribute("room", room);
        model.addAttribute("username", username);
        return "chat";
    }

    /**
     * Handles leaving a chat room.
     *
     * @param roomId the ID of the room to leave
     * @return redirect URL
     */
    @PostMapping("/room/{roomId}/leave")
    public String leaveRoom(@PathVariable String roomId) {
        String username = userService.getCurrentUsername();
        chatService.removeUserFromRoom(roomId, username);
        return "redirect:/chat/rooms";
    }
}

