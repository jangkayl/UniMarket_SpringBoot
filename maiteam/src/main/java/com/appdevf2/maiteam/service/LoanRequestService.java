package com.appdevf2.maiteam.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.appdevf2.maiteam.entity.LoanRequest;
import com.appdevf2.maiteam.entity.Student;
import com.appdevf2.maiteam.repository.LoanRequestRepository;
import com.appdevf2.maiteam.repository.StudentRepository;

@Service
public class LoanRequestService {
    private final LoanRequestRepository loanRequestRepository;
    private final StudentRepository studentRepository;

    public LoanRequestService(LoanRequestRepository loanRequestRepository, StudentRepository studentRepository) {
        this.loanRequestRepository = loanRequestRepository;
        this.studentRepository = studentRepository;
    }

    public LoanRequest saveLoanRequest(LoanRequest loanRequest) {
        if (loanRequest.getBorrower() == null || loanRequest.getBorrower().getStudentId() == null) {
            throw new RuntimeException("Error: Borrower ID is required to create a loan request.");
        }

        Student borrower = studentRepository.findById(loanRequest.getBorrower().getStudentId())
                .orElseThrow(() -> new RuntimeException("Error: Borrower not found with ID " + loanRequest.getBorrower().getStudentId()));
        loanRequest.setBorrower(borrower);


        if (loanRequest.getLender() != null && loanRequest.getLender().getStudentId() != null) {
            Student lender = studentRepository.findById(loanRequest.getLender().getStudentId())
                    .orElseThrow(() -> new RuntimeException("Error: Lender not found with ID " + loanRequest.getLender().getStudentId()));
            loanRequest.setLender(lender);
        } else {
            loanRequest.setLender(null);
        }
        
        return loanRequestRepository.save(loanRequest);
    }

    public List<LoanRequest> getAllLoanRequests() {
        return loanRequestRepository.findAll();
    }

    public LoanRequest updateLoan(Long id, LoanRequest newData) {
        return loanRequestRepository.findById(id).map(loan -> {

            if (newData.getLoanamount() != null) loan.setLoanamount(newData.getLoanamount());
            if (newData.getRepaymentperioddays() != null) loan.setRepaymentperioddays(newData.getRepaymentperioddays());
            if (newData.getInterestrate() != null) loan.setInterestrate(newData.getInterestrate());
            if (newData.getStatus() != null) loan.setStatus(newData.getStatus());
            if (newData.getRequestdate() != null) loan.setRequestdate(newData.getRequestdate());
            if (newData.getApprovaldate() != null) loan.setApprovaldate(newData.getApprovaldate());
            if (newData.getDuedate() != null) loan.setDuedate(newData.getDuedate());
            if (newData.getRepaymentdate() != null) loan.setRepaymentdate(newData.getRepaymentdate());
            if (newData.getPurpose() != null) loan.setPurpose(newData.getPurpose());

            if (newData.getBorrower() != null && newData.getBorrower().getStudentId() != null) {
                Student borrower = studentRepository.findById(newData.getBorrower().getStudentId())
                        .orElseThrow(() -> new RuntimeException("Error: Cannot update. Borrower not found with ID " + newData.getBorrower().getStudentId()));
                loan.setBorrower(borrower);
            }

            if (newData.getLender() != null && newData.getLender().getStudentId() != null) {
                Student lender = studentRepository.findById(newData.getLender().getStudentId())
                        .orElseThrow(() -> new RuntimeException("Error: Cannot update. Lender not found with ID " + newData.getLender().getStudentId()));
                loan.setLender(lender);
            }

            return loanRequestRepository.save(loan);
        }).orElseThrow(() -> new RuntimeException("Error: Loan Request not found with ID " + id));
    }

    public boolean deleteLoan(Long id) {
        if (loanRequestRepository.existsById(id)) {
            loanRequestRepository.deleteById(id);
            return true;
        }
        return false;
    }
}