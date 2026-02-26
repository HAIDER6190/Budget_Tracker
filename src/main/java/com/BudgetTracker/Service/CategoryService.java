package com.BudgetTracker.Service;

import com.BudgetTracker.Exception.AccessDeniedException;
import com.BudgetTracker.Exception.CategoryNotFoundException;
import com.BudgetTracker.dto.CategoryRequestDTO;
import com.BudgetTracker.dto.CategoryResponseDTO;
import com.BudgetTracker.Entity.Category;
import com.BudgetTracker.Entity.User;
import com.BudgetTracker.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserProvider authUserProvider;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Category category = Category.builder()
                .name(dto.getName())
                .type(dto.getType().toUpperCase())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public List<CategoryResponseDTO> getCategoriesByUser() {
        User user = authUserProvider.getCurrentUser();

        return categoryRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long categoryId, CategoryRequestDTO dto) {
        User user = authUserProvider.getCurrentUser();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to update this category");
        }

        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getType() != null) category.setType(dto.getType().toUpperCase());

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        User user = authUserProvider.getCurrentUser();

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this category");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponseDTO mapToResponse(Category c) {
        return new CategoryResponseDTO(c.getId(), c.getName(), c.getType(), c.getUser().getUsername());
    }
}
