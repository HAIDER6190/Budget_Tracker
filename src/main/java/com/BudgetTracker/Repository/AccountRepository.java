package com.BudgetTracker.Repository;

import com.BudgetTracker.Entity.Account;
import com.BudgetTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
