package com.flight.management.controllers;

import com.flight.management.model.Message;
import com.flight.management.model.User;
import com.flight.management.services.MessageService;
import com.flight.management.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.createMessage(message));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long messageId, @RequestBody Message updatedMessage) {
        Message message = messageService.updateMessage(messageId, updatedMessage);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<Message>> getMessagesBySender(@PathVariable Long senderId) {
        Optional<User> sender = userService.getUserById(senderId);
        if (sender.isPresent()) {
            return ResponseEntity.ok(messageService.getMessagesBySender(sender.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<Message>> getMessagesByReceiver(@PathVariable Long receiverId) {
        Optional<User> receiver = userService.getUserById(receiverId);
        if (receiver.isPresent()) {
            return ResponseEntity.ok(messageService.getMessagesByReceiver(receiver.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}