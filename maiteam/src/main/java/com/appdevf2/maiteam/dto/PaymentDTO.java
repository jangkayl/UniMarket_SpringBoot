package com.appdevf2.maiteam.dto;

import java.time.LocalDate;

public class PaymentDTO {

    private Long paymentid;
    private Long payerId;      
    private Long payeeId;      
    private Long transactionId; 
    private Long loanId;        

    private Double amount;
    private String paymentmethod;
    private String paymentreference;
    private String status;
    private LocalDate paymentdate;
    private String gatewayresponse;

    public PaymentDTO() {}

    // Getters and Setters
    public Long getPaymentid() { return paymentid; }
    public void setPaymentid(Long paymentid) { this.paymentid = paymentid; }

    public Long getPayerId() { return payerId; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }

    public Long getPayeeId() { return payeeId; }
    public void setPayeeId(Long payeeId) { this.payeeId = payeeId; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

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