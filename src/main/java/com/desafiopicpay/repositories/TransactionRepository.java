package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<@NonNull Transaction, @NonNull Long> {
    Page<@NonNull Transaction> findAllBySenderId(Long senderId, Pageable pageable);
    Page<@NonNull Transaction> findAllByReceiverId(Long receiverId, Pageable pageable);
    Page<@NonNull Transaction> findAllBySenderIdOrReceiverId(Long senderId, Long receiverId, Pageable pageable);
}
