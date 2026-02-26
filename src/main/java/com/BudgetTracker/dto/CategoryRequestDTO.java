package com.BudgetTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    private String name;

    @NotBlank(message = "Category type is required")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Type must be either INCOME or EXPENSE")
    private String type;
}
