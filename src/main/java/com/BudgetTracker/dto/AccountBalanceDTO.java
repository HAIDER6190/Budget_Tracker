package com.BudgetTracker.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceDTO {
    private Long accountId;
    private String accountName;
    private double totalIncome;
    private double totalExpense;
    private double balance; // net balance
}
