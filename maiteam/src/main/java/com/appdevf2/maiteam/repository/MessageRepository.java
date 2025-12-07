package com.appdevf2.maiteam.repository;

import java.util.List;

import com.appdevf2.maiteam.entity.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find all messages where the user is either sender OR receiver (to build contact list)
    List<Message> findBySender_StudentIdOrReceiver_StudentIdOrderBySentAtDesc(Long senderId, Long receiverId);

    // Find conversation between two specific users
    // (sender=A AND receiver=B) OR (sender=B AND receiver=A)
    List<Message> findBySender_StudentIdAndReceiver_StudentIdOrSender_StudentIdAndReceiver_StudentIdOrderBySentAtAsc(
            Long senderId1, Long receiverId1, Long senderId2, Long receiverId2
    );

    @Modifying
    @Query("DELETE FROM Message m WHERE " +
           "(m.sender.studentId = :user1 AND m.receiver.studentId = :user2) OR " +
           "(m.sender.studentId = :user2 AND m.receiver.studentId = :user1)")
    void deleteConversation(@Param("user1") Long user1, @Param("user2") Long user2);
}
