package com.appdevf2.maiteam.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    // STUDENT ||--o{ MESSAGE : "sends/receives" - sender
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Student sender;

    // STUDENT ||--o{ MESSAGE : "sends/receives" - receiver
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Student receiver;

    // ITEM ||--o{ MESSAGE : about
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = true) // Item ID can be null
    private Item item;

    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "message_type")
    private String messageType; 

    // Constructors
    public Message() {
        this.sentAt = Instant.now();
    }

    public Message(Student sender, Student receiver, String messageContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageContent = messageContent;
        this.sentAt = Instant.now();
    }

    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Student getSender() { return sender; }
    public void setSender(Student sender) { this.sender = sender; }

    public Student getReceiver() { return receiver; }
    public void setReceiver(Student receiver) { this.receiver = receiver; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}