package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Message;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.repository.MessageRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;

    public MessageService(MessageRepository messageRepository, 
                          StudentRepository studentRepository, 
                          ItemRepository itemRepository) {
        this.messageRepository = messageRepository;
        this.studentRepository = studentRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Message createMessage(Message message) {
        // 1. Validate Sender
        if (message.getSender() != null && message.getSender().getStudentId() != null) {
            Student sender = studentRepository.findById(message.getSender().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + message.getSender().getStudentId()));
            message.setSender(sender);
        } else {
            throw new RuntimeException("Sender ID is required.");
        }

        // 2. Validate Receiver
        if (message.getReceiver() != null && message.getReceiver().getStudentId() != null) {
            Student receiver = studentRepository.findById(message.getReceiver().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found with ID: " + message.getReceiver().getStudentId()));
            message.setReceiver(receiver);
        } else {
            throw new RuntimeException("Receiver ID is required.");
        }
        
        // 3. Validate Item (Optional)
        if (message.getItem() != null && message.getItem().getItemId() != null) {
            Item item = itemRepository.findById(message.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + message.getItem().getItemId()));
            message.setItem(item);
        } else {
            message.setItem(null);
        }
        
        if (message.getSentAt() == null) {
            message.setSentAt(Instant.now());
        }
        if (message.getIsRead() == null) {
            message.setIsRead(false);
        }
        
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public Message updateMessage(Long id, Message newMessageData) {
        return messageRepository.findById(id).map(message -> {
            message.setMessageContent(newMessageData.getMessageContent());
            message.setMessageType(newMessageData.getMessageType());
            
            // Only update read status if explicitly provided
            if(newMessageData.getIsRead() != null) {
                message.setIsRead(newMessageData.getIsRead());
            }
            
            return messageRepository.save(message);
        }).orElseThrow(() -> new RuntimeException("Message not found with ID: " + id));
    }

    @Transactional
    public void deleteMessage(Long id) {
        if (messageRepository.existsById(id)) {
            messageRepository.deleteById(id);
        } else {
            throw new RuntimeException("Message not found with ID: " + id);
        }
    }
}