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

import java.time.LocalDate;
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
    private static final String MSG_ORDER_ONGOING = "Item Received: Rental for '%s' has started. Due date: %s.";
    private static final String MSG_ORDER_COMPLETED = "Transaction Completed: %s confirmed receipt of '%s'. Funds have been released.";
    private static final String MSG_ORDER_RETURNING = "Return Initiated: %s is returning '%s'. Please meet to collect it.";
    private static final String MSG_ORDER_RETURNED = "Item Returned: %s confirmed the return of '%s'. Transaction closed.";
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

        if ("Rent".equalsIgnoreCase(transaction.getTransactionType()) 
                && transaction.getItem().getRentalDurationDays() != null 
                && transaction.getItem().getRentalDurationDays() > 0) {
            LocalDate dueDate = LocalDate.now().plusDays(transaction.getItem().getRentalDurationDays());
            transaction.setDueDate(dueDate);
        }

        if (transaction.getNotes() != null && transaction.getNotes().contains("Method: WALLET")) {
            walletService.holdFunds(
                transaction.getBuyer().getStudentId(), 
                transaction.getAmount(), 
                transaction.getItem().getItemName()
            );
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        String message = String.format(MSG_ORDER_PLACED, 
            savedTransaction.getBuyer().getFirstName(),
            savedTransaction.getTransactionType(),
            savedTransaction.getItem().getItemName()
        );
        sendNotification(savedTransaction.getSeller(), "New Order Received", message, "order");

        return savedTransaction;
    }

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

        if (transaction.getNotes().contains("Method: WALLET")) {
            transaction.setStatus("To Meet"); 
        } else {
            transaction.setStatus("To Pay"); 
        }
        transactionRepository.save(transaction);

        String message = String.format(MSG_ORDER_ACCEPTED, transaction.getSeller().getFirstName(), transaction.getItem().getItemName());
        sendNotification(transaction.getBuyer(), "Order Accepted", message, "order");
    }

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

        if (transaction.getNotes().contains("Method: WALLET")) {
            walletService.processPayment(
                transaction.getBuyer().getStudentId(),
                transaction.getSeller().getStudentId(),
                transaction.getAmount(),
                transaction.getItem().getItemName()
            );
        }

        if ("Rent".equalsIgnoreCase(transaction.getTransactionType())) {
            transaction.setStatus("Ongoing");
            String message = String.format(MSG_ORDER_ONGOING, transaction.getItem().getItemName(), transaction.getDueDate());
            sendNotification(transaction.getSeller(), "Item Rented Out", message, "rent");
        } else {
            transaction.setStatus("Completed");

            String message = String.format(MSG_ORDER_COMPLETED, transaction.getBuyer().getFirstName(), transaction.getItem().getItemName());
            sendNotification(transaction.getSeller(), "Transaction Completed", message, "payment");
        }
        
        transactionRepository.save(transaction);
    }

    @Transactional
    public void markAsReturned(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getBuyer().getStudentId().equals(userId)) throw new RuntimeException("Unauthorized.");
        if (!"Ongoing".equalsIgnoreCase(transaction.getStatus())) throw new RuntimeException("Transaction is not ongoing.");

        transaction.setStatus("Returning");
        transactionRepository.save(transaction);

        String message = String.format(MSG_ORDER_RETURNING, transaction.getBuyer().getFirstName(), transaction.getItem().getItemName());
        sendNotification(transaction.getSeller(), "Return Initiated", message, "rent");
    }

    @Transactional
    public void completeReturn(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getSeller().getStudentId().equals(userId)) throw new RuntimeException("Unauthorized.");
        if (!"Returning".equalsIgnoreCase(transaction.getStatus()) && !"Ongoing".equalsIgnoreCase(transaction.getStatus())) {
             throw new RuntimeException("Transaction is not in return phase.");
        }

        transaction.setStatus("Completed");
        transactionRepository.save(transaction);

        Item item = transaction.getItem();
        item.setAvailabilityStatus("AVAILABLE");
        itemRepository.save(item);

        String message = String.format(MSG_ORDER_RETURNED, transaction.getSeller().getFirstName(), transaction.getItem().getItemName());
        sendNotification(transaction.getBuyer(), "Return Confirmed", message, "rent");
    }

    @Transactional
    public void cancelTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ("Completed".equalsIgnoreCase(transaction.getStatus()) || "Cancelled".equalsIgnoreCase(transaction.getStatus())) {
            throw new RuntimeException("Cannot cancel a completed or already cancelled transaction.");
        }
        
        boolean isBuyer = transaction.getBuyer().getStudentId().equals(userId);
        boolean isSeller = transaction.getSeller().getStudentId().equals(userId);
        
        if (!isBuyer && !isSeller) {
            throw new RuntimeException("You are not authorized to cancel this transaction.");
        }

        if (transaction.getNotes() != null && transaction.getNotes().contains("Method: WALLET")) {
            walletService.addFunds(
                transaction.getBuyer().getStudentId(),
                transaction.getAmount(),
                "Refund: " + transaction.getItem().getItemName(),
                "REFUND-" + transactionId
            );
        }

        transaction.setStatus("Cancelled");
        String cancelledBy = isBuyer ? "Buyer" : "Seller";
        transaction.setNotes(transaction.getNotes() + " [Cancelled by " + cancelledBy + "]");
        
        transactionRepository.save(transaction);

        Student recipient = isBuyer ? transaction.getSeller() : transaction.getBuyer();
        Student actor = isBuyer ? transaction.getBuyer() : transaction.getSeller();

        String message = String.format(MSG_ORDER_CANCELLED, 
            transaction.getItem().getItemName(),
            actor.getFirstName()
        );

        sendNotification(recipient, "Transaction Cancelled", message, "order");
    }

    public Optional<Transaction> getActiveTransaction(Long userId1, Long userId2) {
        // Pass arguments in correct order for (Buyer1 & Seller2) OR (Buyer2 & Seller1)
        List<Transaction> history = transactionRepository.findByBuyer_StudentIdAndSeller_StudentIdOrBuyer_StudentIdAndSeller_StudentIdOrderByTransactionIdDesc(
                userId1, userId2, userId2, userId1
        );

        if (history.isEmpty()) return Optional.empty();

        Optional<Transaction> pendingTx = history.stream()
                .filter(t -> "Pending".equalsIgnoreCase(t.getStatus()))
                .findFirst();

        if (pendingTx.isPresent()) {
            return pendingTx;
        }

        return Optional.of(history.get(0));
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

    public List<Transaction> findAll() { return transactionRepository.findAll(); }
    public void deleteById(Long id) { transactionRepository.deleteById(id); }
    public Transaction update(Long id, Transaction newData) { return transactionRepository.save(newData); }
    
    public List<Transaction> getPendingTransactionsForUser(Long userId) {
        return transactionRepository.findByStatusAndBuyer_StudentIdOrStatusAndSeller_StudentId("Pending", userId, "Pending", userId);
    }
    
    public List<Transaction> getUserHistory(Long userId) {
        return transactionRepository.findByBuyer_StudentIdOrSeller_StudentIdOrderByTransactionDateDesc(userId, userId);
    }
}