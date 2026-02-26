package com.BudgetTracker.dto;

import lombok.*;
import java.time.LocalDate;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Double amount;
    private String description;
    private LocalDate transactionDate;
    private String username;       // user name instead of id
    private String categoryName;   // category name instead of id
    private String accountName;    // account name instead of id (optional)
}

