package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Item;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.repository.ItemRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;

    public TransactionService(TransactionRepository transactionRepository, 
                              StudentRepository studentRepository,
                              ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.studentRepository = studentRepository;
        this.itemRepository = itemRepository;
    }

    public Transaction save(Transaction transaction) {
        // 1. Validate and Fetch Buyer
        if (transaction.getBuyer() != null && transaction.getBuyer().getStudentId() != null) {
            Student buyer = studentRepository.findById(transaction.getBuyer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + transaction.getBuyer().getStudentId()));
            transaction.setBuyer(buyer);
        }

        // 2. Validate and Fetch Seller
        if (transaction.getSeller() != null && transaction.getSeller().getStudentId() != null) {
            Student seller = studentRepository.findById(transaction.getSeller().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with ID: " + transaction.getSeller().getStudentId()));
            transaction.setSeller(seller);
        }

        // 3. Validate and Fetch Item
        if (transaction.getItem() != null && transaction.getItem().getItemId() != null) {
            Item item = itemRepository.findById(transaction.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + transaction.getItem().getItemId()));
            transaction.setItem(item);
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction update(Long id, Transaction newData) {
        return transactionRepository.findById(id)
            .map(transaction -> {
                transaction.setAmount(newData.getAmount());
                transaction.setTransactionType(newData.getTransactionType());
                transaction.setStatus(newData.getStatus());
                transaction.setTransactionDate(newData.getTransactionDate());
                transaction.setDueDate(newData.getDueDate());
                transaction.setNotes(newData.getNotes());

                // Update Buyer
                if (newData.getBuyer() != null && newData.getBuyer().getStudentId() != null) {
                    Student buyer = studentRepository.findById(newData.getBuyer().getStudentId())
                            .orElseThrow(() -> new RuntimeException("Buyer not found"));
                    transaction.setBuyer(buyer);
                }

                // Update Seller
                if (newData.getSeller() != null && newData.getSeller().getStudentId() != null) {
                    Student seller = studentRepository.findById(newData.getSeller().getStudentId())
                            .orElseThrow(() -> new RuntimeException("Seller not found"));
                    transaction.setSeller(seller);
                }

                // Update Item
                if (newData.getItem() != null && newData.getItem().getItemId() != null) {
                    Item item = itemRepository.findById(newData.getItem().getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found"));
                    transaction.setItem(item);
                }

                return transactionRepository.save(transaction);
            })
            .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
    }

    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with id " + id);
        }
        transactionRepository.deleteById(id);
    }
}