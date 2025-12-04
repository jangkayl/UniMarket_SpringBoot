package com.appdevf2.maiteam.service;

import com.appdevf2.maiteam.entity.Dispute;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.repository.DisputeRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DisputeService {

    @Autowired
    private DisputeRepository disputeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public Dispute createDispute(Dispute dispute) {
        // 1. Validate Complainant
        if (dispute.getComplainant() != null && dispute.getComplainant().getStudentId() != null) {
            Student complainant = studentRepository.findById(dispute.getComplainant().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Complainant not found with ID: " + dispute.getComplainant().getStudentId()));
            dispute.setComplainant(complainant);
        } else {
            throw new RuntimeException("Complainant ID is required.");
        }

        // 2. Validate Respondent
        if (dispute.getRespondent() != null && dispute.getRespondent().getStudentId() != null) {
            Student respondent = studentRepository.findById(dispute.getRespondent().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Respondent not found with ID: " + dispute.getRespondent().getStudentId()));
            dispute.setRespondent(respondent);
        } else {
            throw new RuntimeException("Respondent ID is required.");
        }

        // 3. Validate Transaction
        if (dispute.getTransaction() != null && dispute.getTransaction().getTransactionId() != null) {
            Transaction transaction = transactionRepository.findById(dispute.getTransaction().getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + dispute.getTransaction().getTransactionId()));
            dispute.setTransaction(transaction);
        } else {
            throw new RuntimeException("Transaction ID is required.");
        }

        return disputeRepository.save(dispute);
    }

    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }

    public Optional<Dispute> getDisputeById(Long id) {
        return disputeRepository.findById(id);
    }

    public Dispute resolveDispute(Long id, String notes) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispute not found with ID: " + id));
        dispute.setStatus("RESOLVED");
        dispute.setResolutionNotes(notes);
        dispute.setResolvedAt(LocalDateTime.now());
        return disputeRepository.save(dispute);
    }

    public void deleteDispute(Long id) {
        if (disputeRepository.existsById(id)) {
            disputeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Dispute not found with ID: " + id);
        }
    }
}