package com.appdevf2.maiteam.repository;

import com.appdevf2.maiteam.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
