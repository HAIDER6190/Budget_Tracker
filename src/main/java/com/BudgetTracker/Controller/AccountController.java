package com.BudgetTracker.Controller;

import com.BudgetTracker.Service.AccountService;
import com.BudgetTracker.dto.AccountBalanceDTO;
import com.BudgetTracker.dto.AccountRequestDTO;
import com.BudgetTracker.dto.AccountResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByUser() {
        return ResponseEntity.ok(accountService.getAccountsByUser());
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody AccountRequestDTO dto) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, dto));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<AccountBalanceDTO> getAccountBalance(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountBalanceDetails(accountId));
    }
}
