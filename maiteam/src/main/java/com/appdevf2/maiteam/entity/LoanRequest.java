package com.appdevf2.maiteam.entity;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "loan_request")
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanid;

    @ManyToOne
    @JoinColumn(name = "borrower_id", referencedColumnName = "studentId")
    private Student borrower;

    @ManyToOne
    @JoinColumn(name = "lender_id", referencedColumnName = "studentId")
    private Student lender;

    private Double loanamount;
    private Integer repaymentperioddays;
    private Double interestrate;
    private String status;
    private LocalDate requestdate;
    private LocalDate approvaldate;
    private LocalDate duedate;
    private LocalDate repaymentdate;
    private String purpose;

    public LoanRequest() {}

    public Long getLoanid() { return loanid; }
    public void setLoanid(Long loanid) { this.loanid = loanid; }

    public Student getBorrower() { return borrower; }
    public void setBorrower(Student borrower) { this.borrower = borrower; }

    public Student getLender() { return lender; }
    public void setLender(Student lender) { this.lender = lender; }

    public Double getLoanamount() { return loanamount; }
    public void setLoanamount(Double loanamount) { this.loanamount = loanamount; }

    public Integer getRepaymentperioddays() { return repaymentperioddays; }
    public void setRepaymentperioddays(Integer repaymentperioddays) { this.repaymentperioddays = repaymentperioddays; }

    public Double getInterestrate() { return interestrate; }
    public void setInterestrate(Double interestrate) { this.interestrate = interestrate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestdate() { return requestdate; }
    public void setRequestdate(LocalDate requestdate) { this.requestdate = requestdate; }

    public LocalDate getApprovaldate() { return approvaldate; }
    public void setApprovaldate(LocalDate approvaldate) { this.approvaldate = approvaldate; }

    public LocalDate getDuedate() { return duedate; }
    public void setDuedate(LocalDate duedate) { this.duedate = duedate; }

    public LocalDate getRepaymentdate() { return repaymentdate; }
    public void setRepaymentdate(LocalDate repaymentdate) { this.repaymentdate = repaymentdate; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}