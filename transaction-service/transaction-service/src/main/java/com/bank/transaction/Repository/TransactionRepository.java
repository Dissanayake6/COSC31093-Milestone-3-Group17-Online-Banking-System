package com.bank.transaction.repository;

import com.bank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUsernameOrderByTimestampDesc(String username);

    List<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount);

    Optional<Transaction> findByTransactionId(String transactionId);
}