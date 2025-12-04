package com.appdevf2.maiteam.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.appdevf2.maiteam.dto.PaymentDTO;
import com.appdevf2.maiteam.entity.Payment;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.entity.Transaction;
import com.appdevf2.maiteam.entity.LoanRequest;
import com.appdevf2.maiteam.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/addPayment")
    public ResponseEntity<PaymentDTO> addPayment(@RequestBody PaymentDTO dto) {
        Payment payment = convertToEntity(dto);
        Payment savedPayment = paymentService.savePayment(payment);
        return new ResponseEntity<>(convertToDTO(savedPayment), HttpStatus.CREATED);
    }

    @GetMapping("/getAllPayments")
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/getPaymentById/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return ResponseEntity.ok(convertToDTO(payment));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/updatePayment/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        Payment payment = convertToEntity(dto);
        Payment updated = paymentService.updatePayment(id, payment);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/deletePayment/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- Helper: Entity to DTO ---
    private PaymentDTO convertToDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentid(entity.getPaymentid());
        dto.setAmount(entity.getAmount());
        dto.setPaymentmethod(entity.getPaymentmethod());
        dto.setPaymentreference(entity.getPaymentreference());
        dto.setStatus(entity.getStatus());
        dto.setPaymentdate(entity.getPaymentdate());
        dto.setGatewayresponse(entity.getGatewayresponse());

        if (entity.getPayer() != null) dto.setPayerId(entity.getPayer().getStudentId());
        if (entity.getPayee() != null) dto.setPayeeId(entity.getPayee().getStudentId());
        if (entity.getTransaction() != null) dto.setTransactionId(entity.getTransaction().getTransactionId());
        if (entity.getLoanRequest() != null) dto.setLoanId(entity.getLoanRequest().getLoanid());

        return dto;
    }

    // --- Helper: DTO to Entity ---
    private Payment convertToEntity(PaymentDTO dto) {
        Payment entity = new Payment();
        entity.setAmount(dto.getAmount());
        entity.setPaymentmethod(dto.getPaymentmethod());
        entity.setPaymentreference(dto.getPaymentreference());
        entity.setStatus(dto.getStatus());
        entity.setPaymentdate(dto.getPaymentdate());
        entity.setGatewayresponse(dto.getGatewayresponse());

        if (dto.getPayerId() != null) {
            Student payer = new Student();
            payer.setStudentId(dto.getPayerId());
            entity.setPayer(payer);
        }
        
        if (dto.getPayeeId() != null) {
            Student payee = new Student();
            payee.setStudentId(dto.getPayeeId());
            entity.setPayee(payee);
        }

        if (dto.getTransactionId() != null) {
            Transaction trans = new Transaction();
            trans.setTransactionId(dto.getTransactionId());
            entity.setTransaction(trans);
        }

        if (dto.getLoanId() != null) {
            LoanRequest loan = new LoanRequest();
            loan.setLoanid(dto.getLoanId());
            entity.setLoanRequest(loan);
        }

        return entity;
    }
}