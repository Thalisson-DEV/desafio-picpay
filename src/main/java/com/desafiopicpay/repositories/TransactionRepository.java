package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.transaction.Transaction;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<@NonNull Transaction, @NonNull Long> {
}
