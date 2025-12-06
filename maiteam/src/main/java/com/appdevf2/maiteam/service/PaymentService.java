package com.appdevf2.maiteam.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.appdevf2.maiteam.entity.Payment;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.entity.LoanRequest;
import com.appdevf2.maiteam.repository.PaymentRepository;
import com.appdevf2.maiteam.repository.StudentRepository;
import com.appdevf2.maiteam.repository.TransactionRepository;
import com.appdevf2.maiteam.repository.LoanRequestRepository;
import jakarta.transaction.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRequestRepository loanRequestRepository;

    public PaymentService(PaymentRepository paymentRepository, 
                          StudentRepository studentRepository,
                          TransactionRepository transactionRepository,
                          LoanRequestRepository loanRequestRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.transactionRepository = transactionRepository;
        this.loanRequestRepository = loanRequestRepository;
    }

    @Transactional
    public Payment savePayment(Payment payment) {
        // 1. Validate Payer (Mandatory)
        if (payment.getPayer() != null && payment.getPayer().getStudentId() != null) {
            Student payer = studentRepository.findById(payment.getPayer().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Payer not found with ID: " + payment.getPayer().getStudentId()));
            payment.setPayer(payer);
        } else {
            throw new RuntimeException("Payer ID is required.");
        }

        // 2. Validate Payee (Mandatory)
        if (payment.getPayee() != null && payment.getPayee().getStudentId() != null) {
            Student payee = studentRepository.findById(payment.getPayee().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Payee not found with ID: " + payment.getPayee().getStudentId()));
            payment.setPayee(payee);
        } else {
            throw new RuntimeException("Payee ID is required.");
        }

        // 3. Validate Transaction (Optional)
        if (payment.getTransaction() != null && payment.getTransaction().getTransactionId() != null) {
            Transaction transaction = transactionRepository.findById(payment.getTransaction().getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + payment.getTransaction().getTransactionId()));
            payment.setTransaction(transaction);
        } else {
            payment.setTransaction(null);
        }

        // 4. Validate Loan Request (Optional)
        if (payment.getLoanRequest() != null && payment.getLoanRequest().getLoanid() != null) {
            LoanRequest loan = loanRequestRepository.findById(payment.getLoanRequest().getLoanid())
                    .orElseThrow(() -> new RuntimeException("Loan Request not found with ID: " + payment.getLoanRequest().getLoanid()));
            payment.setLoanRequest(loan);
        } else {
            payment.setLoanRequest(null);
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Transactional
    public Payment updatePayment(Long id, Payment newData) {
        return paymentRepository.findById(id).map(payment -> {
            payment.setAmount(newData.getAmount());
            payment.setPaymentmethod(newData.getPaymentmethod());
            payment.setPaymentreference(newData.getPaymentreference());
            payment.setStatus(newData.getStatus());
            payment.setPaymentdate(newData.getPaymentdate());
            payment.setGatewayresponse(newData.getGatewayresponse());

            // Update relationships only if provided
            if (newData.getPayer() != null && newData.getPayer().getStudentId() != null) {
                Student payer = studentRepository.findById(newData.getPayer().getStudentId())
                        .orElseThrow(() -> new RuntimeException("Payer not found"));
                payment.setPayer(payer);
            }
            
            return paymentRepository.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    public void deletePayment(Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
    }
}