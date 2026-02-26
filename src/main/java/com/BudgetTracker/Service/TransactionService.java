package com.BudgetTracker.Service;

import com.BudgetTracker.dto.TransactionRequestDTO;
import com.BudgetTracker.dto.TransactionResponseDTO;
import com.BudgetTracker.dto.UserTransactionDTO;
import com.BudgetTracker.Entity.Account;
import com.BudgetTracker.Entity.Category;
import com.BudgetTracker.Entity.Transaction;
import com.BudgetTracker.Entity.User;
import com.BudgetTracker.Exception.AccessDeniedException;
import com.BudgetTracker.Exception.AccountNotFoundException;
import com.BudgetTracker.Exception.CategoryNotFoundException;
import com.BudgetTracker.Exception.TransactionNotFoundException;
import com.BudgetTracker.Repository.AccountRepository;
import com.BudgetTracker.Repository.CategoryRepository;
import com.BudgetTracker.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserProvider authUserProvider;

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Account account = null;
        if (dto.getAccountId() != null) {
            account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + dto.getAccountId()));

            if (!account.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only add transactions to your own accounts");
            }
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + dto.getCategoryId()));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only use your own categories");
        }

        Transaction transaction = Transaction.builder()
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .transactionDate(dto.getTransactionDate())
                .user(user)
                .account(account)
                .category(category)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long transactionId, TransactionRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only update your own transactions");
        }

        if (dto.getAmount() != null) transaction.setAmount(dto.getAmount());
        if (dto.getDescription() != null) transaction.setDescription(dto.getDescription());
        if (dto.getTransactionDate() != null) transaction.setTransactionDate(dto.getTransactionDate());

        if (dto.getAccountId() != null) {
            Account account = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + dto.getAccountId()));
            if (!account.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only assign your own accounts");
            }
            transaction.setAccount(account);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + dto.getCategoryId()));
            if (!category.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only assign your own categories");
            }
            transaction.setCategory(category);
        }

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTransaction(Long transactionId) {
        User user = authUserProvider.getCurrentUser();
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own transactions");
        }

        transactionRepository.delete(transaction);
    }

    public List<TransactionResponseDTO> getTransactionsByAccount(Long accountId) {
        User user = authUserProvider.getCurrentUser();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only view your own account transactions");
        }

        return transactionRepository.findByAccount(account).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserTransactionDTO> getUserTransactions() {
        User user = authUserProvider.getCurrentUser();

        List<Transaction> transactions = transactionRepository.findByUser(user)
                .stream()
                .sorted((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
                .collect(Collectors.toList());

        double[] runningBalance = {0};

        return transactions.stream().map(t -> {
            double amount = t.getAmount();
            String type = t.getCategory().getType();
            runningBalance[0] += type.equalsIgnoreCase("INCOME") ? amount : -amount;

            return UserTransactionDTO.builder()
                    .transactionDate(t.getTransactionDate())
                    .amount(amount)
                    .type(type)
                    .description(t.getDescription())
                    .accountName(t.getAccount() != null ? t.getAccount().getName() : null)
                    .categoryName(t.getCategory().getName())
                    .runningBalance(runningBalance[0])
                    .build();
        }).collect(Collectors.toList());
    }

    private TransactionResponseDTO mapToResponse(Transaction t) {
        return TransactionResponseDTO.builder()
                .amount(t.getAmount())
                .description(t.getDescription())
                .transactionDate(t.getTransactionDate())
                .username(t.getUser().getUsername())
                .categoryName(t.getCategory().getName())
                .accountName(t.getAccount() != null ? t.getAccount().getName() : null)
                .build();
    }
}
