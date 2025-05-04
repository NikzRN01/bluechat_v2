package com.bluechat.controller;

import com.bluechat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling user related HTTP requests.
 * Manages authentication, user profiles, and related views.
 */
@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Root endpoint that redirects to the chat rooms page or login page.
     *
     * @return redirect URL
     */
    @GetMapping("/")
    public String index() {
        String username = userService.getCurrentUsername();
        if ("anonymous".equals(username)) {
            return "redirect:/login";
        }
        return "redirect:/chat/rooms";
    }

    /**
     * Displays the login page.
     *
     * @param error error message if login failed
     * @param logout indicates if user just logged out
     * @param model the Model to add attributes to
     * @return the login view name
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out");
        }
        
        return "login";
    }

    /**
     * Displays the user's profile page.
     *
     * @param model the Model to add attributes to
     * @return the profile view name
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("username", userService.getCurrentUsername());
        model.addAttribute("isAdmin", userService.hasRole("ADMIN"));
        return "profile";
    }

    /**
     * Note: This is a placeholder for user registration.
     * For a real application, you would implement actual user registration.
     * Since we're using in-memory authentication for this demo, this just redirects to login.
     *
     * @return redirect URL
     */
    @GetMapping("/register")
    public String register() {
        return "redirect:/login";
    }
}

