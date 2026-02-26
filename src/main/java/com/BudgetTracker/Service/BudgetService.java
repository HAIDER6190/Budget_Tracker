package com.BudgetTracker.Service;

import com.BudgetTracker.dto.BudgetRequestDTO;
import com.BudgetTracker.dto.BudgetResponseDTO;
import com.BudgetTracker.Entity.Budget;
import com.BudgetTracker.Entity.Category;
import com.BudgetTracker.Entity.User;
import com.BudgetTracker.Exception.AccessDeniedException;
import com.BudgetTracker.Exception.BudgetNotFoundException;
import com.BudgetTracker.Exception.CategoryNotFoundException;
import com.BudgetTracker.Repository.BudgetRepository;
import com.BudgetTracker.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserProvider authUserProvider;

    @Transactional
    public BudgetResponseDTO createBudget(BudgetRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + dto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot use another user's category");
        }

        Budget budget = Budget.builder()
                .amount(dto.getAmount())
                .month(dto.getMonth())
                .year(dto.getYear())
                .user(user)
                .category(category)
                .build();

        Budget saved = budgetRepository.save(budget);
        return mapToResponse(saved);
    }

    public List<BudgetResponseDTO> getBudgetsByUser() {
        User user = authUserProvider.getCurrentUser();

        return budgetRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BudgetResponseDTO updateBudget(Long budgetId, BudgetRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this budget");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + dto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot use another user's category");
        }

        budget.setAmount(dto.getAmount());
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());
        budget.setCategory(category);

        Budget updated = budgetRepository.save(budget);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteBudget(Long budgetId) {
        User user = authUserProvider.getCurrentUser();

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    private BudgetResponseDTO mapToResponse(Budget b) {
        return BudgetResponseDTO.builder()
                .id(b.getId())
                .amount(b.getAmount())
                .month(b.getMonth())
                .year(b.getYear())
                .username(b.getUser().getUsername())
                .categoryName(b.getCategory().getName())
                .build();
    }
}
