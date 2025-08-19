package com.flight.management.services;

import com.flight.management.model.Message;
import com.flight.management.model.User;
import com.flight.management.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long messageId) {
        return messageRepository.findById(messageId);
    }

    public Message createMessage(Message message) {
        message.setTimestamp(LocalDateTime.now()); // Set timestamp on creation
        return messageRepository.save(message);
    }

    public Message updateMessage(Long messageId, Message updatedMessage) {
        if (messageRepository.existsById(messageId)) {
            updatedMessage.setMessageId(messageId);
            return messageRepository.save(updatedMessage);
        }
        return null; // Or throw an exception
    }

    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    public List<Message> getMessagesBySender(User sender) {
        return messageRepository.findBySender(sender);
    }

    public List<Message> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiver(receiver);
    }

    // Add methods for specific business logic related to Messages
}