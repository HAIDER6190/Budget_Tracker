package com.BudgetTracker.Repository;

import com.BudgetTracker.Entity.Account;
import com.BudgetTracker.Entity.Transaction;
import com.BudgetTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find all transactions for a given account
    List<Transaction> findByAccount(Account account);

    // Find all transactions for a given user
    List<Transaction> findByUser(User user);
}
