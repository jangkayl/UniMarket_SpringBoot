package com.appdevf2.maiteam.repository;

import com.appdevf2.maiteam.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByStudent_StudentId(Long studentId);
}