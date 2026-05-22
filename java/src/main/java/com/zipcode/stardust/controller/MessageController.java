package com.zipcode.stardust.controller;

import com.zipcode.stardust.model.Message;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.UserRepository;
import com.zipcode.stardust.service.MessageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @GetMapping("/inbox")
    public String inbox(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        model.addAttribute("messages", messageService.getInbox(currentUser));
        return "messages/inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        model.addAttribute("messages", messageService.getSentMessages(currentUser));
        return "messages/sent";
    }

    @GetMapping("/compose")
    public String composeForm() {
        return "messages/compose";
    }

    @PostMapping("/compose")
    public String sendMessage(
            @RequestParam String receiverUsername,
            @RequestParam String subject,
            @RequestParam String body,
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        messageService.sendMessage(currentUser, receiverUsername, subject, body);

        return "redirect:/messages/sent";
    }

    @GetMapping("/{id}")
    public String viewMessage(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        Message message = messageService.getMessageForUser(id, currentUser);

        model.addAttribute("message", message);
        return "messages/view";
    }
}