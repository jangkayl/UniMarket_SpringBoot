package com.appdevf2.maiteam.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentid;

    @ManyToOne
    @JoinColumn(name = "payer_id", referencedColumnName = "studentId", nullable = false)
    private Student payer;

    @ManyToOne
    @JoinColumn(name = "payee_id", referencedColumnName = "studentId", nullable = false)
    private Student payee;

    @ManyToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "transactionId")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "loan_id", referencedColumnName = "loanid")
    private LoanRequest loanRequest;

    private Double amount;
    private String paymentmethod;
    private String paymentreference;
    private String status;
    private LocalDate paymentdate;

    @Column(columnDefinition = "TEXT")
    private String gatewayresponse;

    public Payment() {}

    // Getters and Setters
    public Long getPaymentid() { return paymentid; }
    public void setPaymentid(Long paymentid) { this.paymentid = paymentid; }

    public Student getPayer() { return payer; }
    public void setPayer(Student payer) { this.payer = payer; }

    public Student getPayee() { return payee; }
    public void setPayee(Student payee) { this.payee = payee; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public LoanRequest getLoanRequest() { return loanRequest; }
    public void setLoanRequest(LoanRequest loanRequest) { this.loanRequest = loanRequest; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentmethod() { return paymentmethod; }
    public void setPaymentmethod(String paymentmethod) { this.paymentmethod = paymentmethod; }

    public String getPaymentreference() { return paymentreference; }
    public void setPaymentreference(String paymentreference) { this.paymentreference = paymentreference; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaymentdate() { return paymentdate; }
    public void setPaymentdate(LocalDate paymentdate) { this.paymentdate = paymentdate; }

    public String getGatewayresponse() { return gatewayresponse; }
    public void setGatewayresponse(String gatewayresponse) { this.gatewayresponse = gatewayresponse; }
}