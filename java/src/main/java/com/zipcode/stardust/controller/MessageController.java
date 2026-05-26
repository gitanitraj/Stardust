package com.zipcode.stardust.controller;

import com.zipcode.stardust.model.Message;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.UserRepository;
import com.zipcode.stardust.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            return null;
        }

        String username = principal.getName();

        return userRepository.findByUsername(username)
                .orElse(null);
    }

    @GetMapping
    public String messagesHome() {
        return "redirect:/messages/inbox";
    }

    @GetMapping("/inbox")
    public String inbox(Model model, Principal principal) {
        User currentUser = getCurrentUser(principal);

        if (currentUser == null) {
            return "redirect:/loginform";
        }

        model.addAttribute("messages", messageService.getInbox(currentUser));
        return "messages/inbox";
    }

    @GetMapping("/sent")
    public String sent(Model model, Principal principal) {
        User currentUser = getCurrentUser(principal);

        if (currentUser == null) {
            return "redirect:/loginform";
        }

        model.addAttribute("messages", messageService.getSentMessages(currentUser));
        return "messages/sent";
    }

    @GetMapping("/compose")
    public String composeForm(Principal principal) {
        if (principal == null) {
            return "redirect:/loginform";
        }

        return "messages/compose";
    }

    @PostMapping("/compose")
    public String sendMessage(
            @RequestParam String receiverUsername,
            @RequestParam String subject,
            @RequestParam String body,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser(principal);

        if (currentUser == null) {
            return "redirect:/loginform";
        }

        try {
            messageService.sendMessage(currentUser, receiverUsername, subject, body);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Message sent successfully!");

            return "redirect:/messages/sent";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not send message. Please check the username.");

            return "redirect:/messages/compose";
        }
    }

    @GetMapping("/{id}")
    public String viewMessage(
            @PathVariable Long id,
            Model model,
            Principal principal) {
        User currentUser = getCurrentUser(principal);

        if (currentUser == null) {
            return "redirect:/loginform";
        }

        Message message = messageService.getMessageForUser(id, currentUser);

        model.addAttribute("message", message);
        return "messages/view";
    }
}