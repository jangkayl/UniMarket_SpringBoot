package com.appdevf2.maiteam.repository;

import com.appdevf2.maiteam.entity.WalletTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWallet_WalletIdOrderByTransactionDateDesc(Long walletId);
    
    // FIX: Changed 'Wallet_StudentId' to 'Wallet_WalletId' to match the Entity structure correctly
    Optional<WalletTransaction> findTopByWallet_WalletIdAndStatusAndDescriptionContainingOrderByTransactionDateDesc(
        Long walletId, String status, String descriptionPart
    );
}