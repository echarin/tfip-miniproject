package ibf2022.tfipminiproject.services;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.ExpenseDTO;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.Expense;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.exceptions.EntityProcessingException;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.ExpenseMapper;
import ibf2022.tfipminiproject.repositories.CategoryRepository;
import ibf2022.tfipminiproject.repositories.ExpenseRepository;
import ibf2022.tfipminiproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    
    public Page<ExpenseDTO> getAllExpensesByUser(
        UUID userId, LocalDate from, LocalDate to, Pageable pageable
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Page<Expense> expenses = expenseRepository.findAllByUserAndDateBetween(user, from, to, pageable);
        return expenses.map(expenseMapper::expenseToExpenseDTO);
    }

    @Transactional
    public ExpenseDTO save(UUID userId, UUID categoryId, ExpenseDTO expenseDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean userOwnsCategory = categoryRepository.existsByUserAndId(user, categoryId);

        if (!userOwnsCategory) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }
        
        Expense expense = expenseMapper.expenseDTOToExpense(expenseDTO);
        category.addExpense(expense);
        categoryRepository.save(category);
        // You do not need to flush and refresh the category
        // Since you are using CascadeType.ALL

        // Hibernate should also update the ID in the expense entity
        UUID expenseId = expense.getId();
        if (expenseId == null) {
            throw new EntityProcessingException("Expense ID not generated after save");
        }

        Expense savedExpense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        return expenseMapper.expenseToExpenseDTO(savedExpense);
    }

    @Transactional
    public void delete(UUID userId, UUID expenseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        boolean userOwnsExpense = expenseRepository.existsByUserAndId(user, expenseId);

        if (!userOwnsExpense) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        
        Category category = expense.getCategory();
        category.removeExpense(expense);
        categoryRepository.save(category);
        // orphanRemoval = true
    } 
}
