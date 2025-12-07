package com.appdevf2.maiteam.controller;

import com.appdevf2.maiteam.dto.MessageDTO;
import com.appdevf2.maiteam.entity.Message;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO dto) {
        // Map DTO to Entity
        Message message = convertToEntity(dto);
        // Save
        Message savedMessage = messageService.createMessage(message);
        // Return DTO
        return ResponseEntity.ok(convertToDTO(savedMessage));
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        
        List<Message> conversation = messageService.getConversation(userId1, userId2);
        
        List<MessageDTO> dtos = conversation.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/contacts/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getContacts(@PathVariable Long userId) {
        // We return a simplified list of students/contacts, not full MessageDTOs
        List<Student> contacts = messageService.getContacts(userId);
        
        List<Map<String, Object>> contactDtos = contacts.stream().map(student -> {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", student.getStudentId());
            map.put("firstName", student.getFirstName());
            map.put("lastName", student.getLastName());
            map.put("profilePicture", student.getProfilePicture());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(contactDtos);
    }

    @PostMapping("/addMessage")
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO dto) {
        Message message = convertToEntity(dto);
        Message savedMessage = messageService.createMessage(message);
        return new ResponseEntity<>(convertToDTO(savedMessage), HttpStatus.CREATED);
    }

    @GetMapping("/getAllMessages")
    public List<MessageDTO> getAllMessages() {
        return messageService.getAllMessages().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getMessageById/{id}")
    public ResponseEntity<MessageDTO> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/updateMessage/{id}")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable Long id, @RequestBody MessageDTO dto) {
        Message message = convertToEntity(dto);
        Message updated = messageService.updateMessage(id, message);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/deleteMessage/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/conversation")
    public ResponseEntity<String> deleteConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        try {
            messageService.deleteConversation(userId1, userId2);
            return ResponseEntity.ok("Conversation deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting conversation: " + e.getMessage());
        }
    }

    // --- Helper: Entity to DTO ---
    private MessageDTO convertToDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(entity.getMessageId());
        dto.setMessageContent(entity.getMessageContent());
        dto.setSentAt(entity.getSentAt());
        dto.setIsRead(entity.getIsRead());
        dto.setMessageType(entity.getMessageType());

        if (entity.getSender() != null) {
            dto.setSenderId(entity.getSender().getStudentId());
            dto.setSenderName(entity.getSender().getFirstName() + " " + entity.getSender().getLastName());
            dto.setSenderProfilePicture(entity.getSender().getProfilePicture());
        }
        
        if (entity.getReceiver() != null) {
            dto.setReceiverId(entity.getReceiver().getStudentId());
            dto.setReceiverName(entity.getReceiver().getFirstName() + " " + entity.getReceiver().getLastName());
            dto.setReceiverProfilePicture(entity.getReceiver().getProfilePicture());
        }
        
        if (entity.getItem() != null) {
            dto.setItemId(entity.getItem().getItemId());
        }

        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Message convertToEntity(MessageDTO dto) {
        Message entity = new Message();
        entity.setMessageContent(dto.getMessageContent());
        entity.setMessageType(dto.getMessageType());
        entity.setIsRead(dto.getIsRead());
        
        if (dto.getSenderId() != null) {
            Student sender = new Student();
            sender.setStudentId(dto.getSenderId());
            entity.setSender(sender);
        }
        
        if (dto.getReceiverId() != null) {
            Student receiver = new Student();
            receiver.setStudentId(dto.getReceiverId());
            entity.setReceiver(receiver);
        }
        
        if (dto.getItemId() != null) {
            Item item = new Item();
            item.setItemId(dto.getItemId());
            entity.setItem(item);
        }

        return entity;
    }
}