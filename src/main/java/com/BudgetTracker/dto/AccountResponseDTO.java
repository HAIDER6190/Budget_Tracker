package com.BudgetTracker.dto;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private String name;
    private String type;
    private String username; // changed from userId
}
