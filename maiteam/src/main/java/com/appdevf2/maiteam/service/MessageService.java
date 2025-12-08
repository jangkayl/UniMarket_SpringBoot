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
import java.util.*;

import org.springframework.context.annotation.Lazy;

import com.appdevf2.maiteam.entity.Notification;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService; 

    public MessageService(MessageRepository messageRepository, 
                          StudentRepository studentRepository, 
                          ItemRepository itemRepository,
                          @Lazy NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.studentRepository = studentRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findBySender_StudentIdAndReceiver_StudentIdOrSender_StudentIdAndReceiver_StudentIdOrderBySentAtAsc(
                userId1, userId2, userId2, userId1
        );
    }

    public List<Student> getContacts(Long userId) {
        // Fetch all messages involving this user
        List<Message> messages = messageRepository.findBySender_StudentIdOrReceiver_StudentIdOrderBySentAtDesc(userId, userId);
        
        Set<Long> contactIds = new HashSet<>();
        List<Student> contacts = new ArrayList<>();

        for (Message msg : messages) {
            Student otherParty = msg.getSender().getStudentId().equals(userId) ? msg.getReceiver() : msg.getSender();
            if (!contactIds.contains(otherParty.getStudentId())) {
                contactIds.add(otherParty.getStudentId());
                contacts.add(otherParty);
            }
        }
        return contacts;
    }

    @Transactional
    public Message createMessage(Message message) {
        // 1. Validate Sender
        if (message.getSender() != null && message.getSender().getStudentId() != null) {
            Student sender = studentRepository.findById(message.getSender().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Sender not found"));
            message.setSender(sender);
        }

        // 2. Validate Receiver
        if (message.getReceiver() != null && message.getReceiver().getStudentId() != null) {
            Student receiver = studentRepository.findById(message.getReceiver().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));
            message.setReceiver(receiver);
        }

        // 3. Validate Item & Create Notification
        if (message.getItem() != null && message.getItem().getItemId() != null) {
            Item item = itemRepository.findById(message.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            message.setItem(item);

            // --- NOTIFICATION TRIGGER ---
            // Only trigger if it's an inquiry about an item
            
            Notification notif = new Notification();
            notif.setStudent(message.getReceiver()); // Seller gets notified
            notif.setTitle("New Inquiry");
            notif.setMessage(message.getSender().getFirstName() + " is asking about '" + item.getItemName() + "'");
            notif.setType("message");
            notif.setRead(false);
            
            notificationService.createNotification(notif);
        } else {
            message.setItem(null);
        }

        if (message.getSentAt() == null) {
            message.setSentAt(Instant.now());
        }
        message.setIsRead(false);

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

    @Transactional
    public void deleteConversation(Long userId1, Long userId2) {
        messageRepository.deleteConversation(userId1, userId2);
    }

}