package com.appdevf2.maiteam.repository;

import java.util.List;

import com.appdevf2.maiteam.entity.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// MessageRepository interface handles standard CRUD operations for the Message entity.
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find messages between two users
    List<Message> findBySender_StudentIdAndReceiver_StudentId(Long senderId, Long receiverId);
    
    // Find messages related to a specific item
    List<Message> findByItem_ItemId(Long itemId);
}
