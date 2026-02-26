package com.BudgetTracker.Service;

import com.BudgetTracker.Entity.Transaction;
import com.BudgetTracker.Exception.AccessDeniedException;
import com.BudgetTracker.Exception.AccountNotFoundException;
import com.BudgetTracker.Repository.TransactionRepository;
import com.BudgetTracker.dto.AccountBalanceDTO;
import com.BudgetTracker.dto.AccountRequestDTO;
import com.BudgetTracker.dto.AccountResponseDTO;
import com.BudgetTracker.Entity.Account;
import com.BudgetTracker.Entity.User;
import com.BudgetTracker.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuthenticatedUserProvider authUserProvider;

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Account account = Account.builder()
                .name(dto.getName())
                .type(dto.getType())
                .user(user)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public List<AccountResponseDTO> getAccountsByUser() {
        User user = authUserProvider.getCurrentUser();

        return accountRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponseDTO updateAccount(Long accountId, AccountRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to update this account");
        }

        if (dto.getName() != null) account.setName(dto.getName());
        if (dto.getType() != null) account.setType(dto.getType());

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        User user = authUserProvider.getCurrentUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this account");
        }

        accountRepository.delete(account);
    }

    public AccountBalanceDTO getAccountBalanceDetails(Long accountId) {
        User user = authUserProvider.getCurrentUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to view this account");
        }

        List<Transaction> transactions = transactionRepository.findByAccount(account);

        double totalIncome = transactions.stream()
                .filter(t -> t.getCategory().getType().equalsIgnoreCase("INCOME"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getCategory().getType().equalsIgnoreCase("EXPENSE"))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        return AccountBalanceDTO.builder()
                .accountId(account.getId())
                .accountName(account.getName())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }

    private AccountResponseDTO mapToResponse(Account a) {
        return AccountResponseDTO.builder()
                .id(a.getId())
                .name(a.getName())
                .type(a.getType())
                .username(a.getUser().getUsername())
                .build();
    }
}
