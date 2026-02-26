package com.BudgetTracker.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTransactionDTO {
    private LocalDate transactionDate;
    private double amount;
    private String type; // INCOME or EXPENSE
    private String description;
    private String accountName;
    private String categoryName;
    private double runningBalance; // optional
}
