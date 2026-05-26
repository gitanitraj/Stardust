package com.zipcode.stardust.service;

import com.zipcode.stardust.model.Message;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.MessageRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public List<Message> getInbox(User user) {
        Objects.requireNonNull(user, "user cannot be null");
        return messageRepository.findByReceiverOrderBySentAtDesc(user);
    }

    public List<Message> getSentMessages(User user) {
        Objects.requireNonNull(user, "user cannot be null");
        return messageRepository.findBySenderOrderBySentAtDesc(user);
    }

    public Message sendMessage(User sender, String receiverUsername, String subject, String body) {
        Objects.requireNonNull(sender, "sender cannot be null");

        if (receiverUsername == null || receiverUsername.trim().isEmpty()) {
            throw new RuntimeException("Receiver username cannot be empty");
        }

        if (subject == null || subject.trim().isEmpty()) {
            throw new RuntimeException("Subject cannot be empty");
        }

        if (body == null || body.trim().isEmpty()) {
            throw new RuntimeException("Message body cannot be empty");
        }

        User receiver = userRepository.findByUsername(receiverUsername.trim())
                .orElseThrow(() -> new RuntimeException("User not found: " + receiverUsername));

        /*
         * This prevents a user from sending a message to themselves.
         * If you WANT to allow self-messages, delete this if block.
         */
        if (receiver.getId().equals(sender.getId())) {
            throw new RuntimeException("You cannot send a message to yourself");
        }

        Message message = new Message(
                subject.trim(),
                body.trim(),
                sender,
                receiver
        );

        return messageRepository.save(message);
    }

    public Message getMessageForUser(Long messageId, User currentUser) {
        Objects.requireNonNull(messageId, "messageId cannot be null");
        Objects.requireNonNull(currentUser, "currentUser cannot be null");

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        boolean isSender = message.getSender().getId().equals(currentUser.getId());
        boolean isReceiver = message.getReceiver().getId().equals(currentUser.getId());

        if (!isSender && !isReceiver) {
            throw new RuntimeException("You are not allowed to view this message");
        }

        if (isReceiver && !message.isReadStatus()) {
            message.setReadStatus(true);
            messageRepository.save(message);
        }

        return message;
    }
}