package com.appdevf2.maiteam.dto;

import java.time.Instant;

public class MessageDTO {

    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private Long itemId;

    private String senderName;
    private String senderProfilePicture;
    private String receiverName;
    private String receiverProfilePicture;

    private String messageContent;
    private Instant sentAt;
    private Boolean isRead;
    private String messageType;

    public MessageDTO() {}

    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderProfilePicture() { return senderProfilePicture; }
    public void setSenderProfilePicture(String senderProfilePicture) { this.senderProfilePicture = senderProfilePicture; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverProfilePicture() { return receiverProfilePicture; }
    public void setReceiverProfilePicture(String receiverProfilePicture) { this.receiverProfilePicture = receiverProfilePicture; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}