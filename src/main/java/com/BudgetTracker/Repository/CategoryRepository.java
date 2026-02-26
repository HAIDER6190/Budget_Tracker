package com.BudgetTracker.Repository;

import com.BudgetTracker.Entity.Category;
import com.BudgetTracker.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 🔥 Add this so your service compiles
    List<Category> findByUser(User user);
}
