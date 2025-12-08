package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Notification;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.repository.ItemRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;
    private final WalletService walletService;

    // Constants
    private static final String MSG_ORDER_PLACED = "New Order: %s wants to %s your item '%s'.";
    private static final String MSG_ORDER_ACCEPTED = "Order Accepted: %s has accepted your offer for '%s'. Please arrange the meetup.";
    private static final String MSG_ORDER_COMPLETED = "Transaction Completed: %s confirmed receipt of '%s'. Funds have been released.";
    private static final String MSG_ORDER_CANCELLED = "Order Cancelled: The transaction for '%s' has been cancelled by %s.";

    public TransactionService(TransactionRepository transactionRepository, 
                              StudentRepository studentRepository,
                              ItemRepository itemRepository,
                              NotificationService notificationService,
                              @Lazy WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.studentRepository = studentRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
        this.walletService = walletService;
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        // ... (Existing validation logic) ...
        if (transaction.getBuyer() != null && transaction.getBuyer().getStudentId() != null) {
            Student buyer = studentRepository.findById(transaction.getBuyer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Buyer not found"));
            transaction.setBuyer(buyer);
        }
        if (transaction.getSeller() != null && transaction.getSeller().getStudentId() != null) {
            Student seller = studentRepository.findById(transaction.getSeller().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Seller not found"));
            transaction.setSeller(seller);
        }
        if (transaction.getItem() != null && transaction.getItem().getItemId() != null) {
            Item item = itemRepository.findById(transaction.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            transaction.setItem(item);
        }

        // Hold funds if Wallet
        if (transaction.getNotes() != null && transaction.getNotes().contains("Method: WALLET")) {
            walletService.holdFunds(
                transaction.getBuyer().getStudentId(), 
                transaction.getAmount(), 
                transaction.getItem().getItemName()
            );
        }

        Transaction saved = transactionRepository.save(transaction);
        
        // Notify Seller
        String message = String.format(MSG_ORDER_PLACED, saved.getBuyer().getFirstName(), saved.getTransactionType(), saved.getItem().getItemName());
        sendNotification(saved.getSeller(), "New Order", message, "order");
        
        return saved;
    }

    // --- 1. SELLER ACCEPTS ---
    @Transactional
    public void acceptTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getSeller().getStudentId().equals(userId)) {
            throw new RuntimeException("Only the seller can accept this order.");
        }

        if (!"Pending".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Transaction is not pending.");
        }

        // Determine next status based on payment method in notes
        if (transaction.getNotes().contains("Method: WALLET")) {
            transaction.setStatus("To Meet"); // Money already held, just meet
        } else {
            transaction.setStatus("To Pay"); // Meetup/Cash: Need to pay
        }

        transactionRepository.save(transaction);

        // Notify Buyer
        String message = String.format(MSG_ORDER_ACCEPTED, transaction.getSeller().getFirstName(), transaction.getItem().getItemName());
        sendNotification(transaction.getBuyer(), "Order Accepted", message, "order");
    }

    // --- 2. SELLER DECLINES / USER CANCELS (Existing) ---
    @Transactional
    public void cancelTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Allow cancellation if Pending OR Accepted (before completion)
        // Adjust logic if you want to restrict cancellation after acceptance
        if ("Completed".equalsIgnoreCase(transaction.getStatus()) || "Cancelled".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Cannot cancel a completed or already cancelled transaction.");
        }

        boolean isBuyer = transaction.getBuyer().getStudentId().equals(userId);
        boolean isSeller = transaction.getSeller().getStudentId().equals(userId);
        if (!isBuyer && !isSeller) throw new RuntimeException("Unauthorized.");

        // Refund Logic
        if (transaction.getNotes().contains("Method: WALLET")) {
            walletService.addFunds(
                transaction.getBuyer().getStudentId(),
                transaction.getAmount(),
                "Refund: " + transaction.getItem().getItemName(),
                "REF-" + transactionId
            );
        }

        transaction.setStatus("Cancelled");
        transaction.setNotes(transaction.getNotes() + " [Cancelled by " + (isBuyer ? "Buyer" : "Seller") + "]");
        transactionRepository.save(transaction);

        // Notify other party
        Student recipient = isBuyer ? transaction.getSeller() : transaction.getBuyer();
        Student actor = isBuyer ? transaction.getBuyer() : transaction.getSeller();
        String message = String.format(MSG_ORDER_CANCELLED, transaction.getItem().getItemName(), actor.getFirstName());
        sendNotification(recipient, "Order Cancelled", message, "order");
    }

    // --- 3. BUYER CONFIRMS RECEIPT (RELEASE FUNDS) ---
    @Transactional
    public void confirmTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBuyer().getStudentId().equals(userId)) {
            throw new RuntimeException("Only the buyer can confirm receipt.");
        }

        if ("Completed".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Transaction already completed.");
        }

        // RELEASE FUNDS LOGIC
        if (transaction.getNotes().contains("Method: WALLET")) {
            // Transfer held amount to Seller
            walletService.addFunds(
                transaction.getSeller().getStudentId(),
                transaction.getAmount(),
                "Sale: " + transaction.getItem().getItemName(),
                "SALE-" + transactionId
            );
        }

        transaction.setStatus("Completed");
        transactionRepository.save(transaction);

        // Notify Seller
        String message = String.format(MSG_ORDER_COMPLETED, transaction.getBuyer().getFirstName(), transaction.getItem().getItemName());
        sendNotification(transaction.getSeller(), "Funds Released", message, "payment");
    }

    private void sendNotification(Student recipient, String title, String message, String type) {
        Notification notif = new Notification();
        notif.setStudent(recipient);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setType(type);
        notif.setRead(false);
        notificationService.createNotification(notif);
    }
    
    // ... (Keep existing findAll, deleteById, getters) ...
    public List<Transaction> findAll() { return transactionRepository.findAll(); }
    public void deleteById(Long id) { transactionRepository.deleteById(id); }
    public Transaction update(Long id, Transaction newData) { return transactionRepository.save(newData); } // Simplified for brevity
    
    public Optional<Transaction> getActiveTransaction(Long userId1, Long userId2) {
        List<Transaction> history = transactionRepository.findByBuyer_StudentIdAndSeller_StudentIdOrBuyer_StudentIdAndSeller_StudentIdOrderByTransactionIdDesc(userId1, userId2, userId2, userId1);
        if (history.isEmpty()) return Optional.empty();
        
        // Prioritize non-final states
        return history.stream()
                .filter(t -> !t.getStatus().equalsIgnoreCase("Cancelled") && !t.getStatus().equalsIgnoreCase("Completed"))
                .findFirst()
                .or(() -> Optional.of(history.get(0)));
    }

    public List<Transaction> getPendingTransactionsForUser(Long userId) {
         return transactionRepository.findByStatusAndBuyer_StudentIdOrStatusAndSeller_StudentId("Pending", userId, "Pending", userId);
    }
    
    public List<Transaction> getUserHistory(Long userId) {
        return transactionRepository.findByBuyer_StudentIdOrSeller_StudentIdOrderByTransactionDateDesc(userId, userId);
    }
}