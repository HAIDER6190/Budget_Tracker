package com.BudgetTracker.Controller;

import com.BudgetTracker.Service.BudgetService;
import com.BudgetTracker.dto.BudgetRequestDTO;
import com.BudgetTracker.dto.BudgetResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createBudget(@Valid @RequestBody BudgetRequestDTO dto) {
        return ResponseEntity.ok(budgetService.createBudget(dto));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getBudgetsByUser() {
        return ResponseEntity.ok(budgetService.getBudgetsByUser());
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetRequestDTO dto) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetId, dto));
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
