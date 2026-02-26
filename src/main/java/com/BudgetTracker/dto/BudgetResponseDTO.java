package com.BudgetTracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponseDTO {
    private Long id;
    private Double amount;
    private Integer month;
    private Integer year;
    private String username;       // User who owns the budget
    private String categoryName;   // Name of the category
}
