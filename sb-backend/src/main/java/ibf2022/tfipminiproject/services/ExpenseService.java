package ibf2022.tfipminiproject.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.ExpenseDTO;
import ibf2022.tfipminiproject.exceptions.EntityProcessingException;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.ExpenseMapper;
import ibf2022.tfipminiproject.sqlentities.Category;
import ibf2022.tfipminiproject.sqlentities.Expense;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.CategoryRepository;
import ibf2022.tfipminiproject.sqlrepositories.ExpenseRepository;
import ibf2022.tfipminiproject.sqlrepositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    
    public ExpenseDTO getExpense(UUID userId, UUID expenseId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        boolean userOwnsExpense = expenseRepository.existsByUserAndId(user, expenseId);

        if (!userOwnsExpense) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        
        return expenseMapper.expenseToExpenseDTO(expense);
    }
    
    public Page<ExpenseDTO> getAllExpensesByUser(
        UUID userId, LocalDate from, LocalDate to, Pageable pageable
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Page<Expense> expenses = expenseRepository.findAllByUserAndDateBetween(user, from, to, pageable);
        return expenses.map(expenseMapper::expenseToExpenseDTO);
    }

    public List<ExpenseDTO> getAllExpensesByCategory(UUID userId, UUID categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        boolean userOwnsCategory = categoryRepository.existsByUserAndId(user, categoryId);

        if (!userOwnsCategory) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        List<Expense> expenses = expenseRepository.findAllByCategory(category);
        return expenses
                .stream()
                .map(expenseMapper::expenseToExpenseDTO)
                .collect(Collectors.toList());
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
        
        // Business logic: Adding an expense with an amount will subtract from category
        Expense expense = expenseMapper.expenseDTOToExpense(expenseDTO);
        BigDecimal expenseAmount = expense.getAmount();
        BigDecimal categoryAmount = category.getBudgetedAmount();

        if (expenseAmount == null || categoryAmount == null) {
            throw new EntityProcessingException("Budgeted amounts were not set");
        }
        
        category.setBudgetedAmount(categoryAmount.subtract(expenseAmount));
        category.addExpense(expense);
        categoryRepository.save(category);

        Expense savedExpense = category.getExpenses().get(category.getExpenses().size() - 1);
        UUID expenseId = savedExpense.getId();
        if (expenseId == null) {
            throw new EntityProcessingException("Expense ID not generated after save");
        }

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

        // Business logic: Deleting an expense with an amount will return it to category pool
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        Category category = expense.getCategory();
        BigDecimal expenseAmount = expense.getAmount();
        BigDecimal categoryAmount = category.getBudgetedAmount();

        if (expenseAmount == null || categoryAmount == null) {
            throw new EntityProcessingException("Budgeted amounts were not set");
        }
        
        category.setBudgetedAmount(categoryAmount.add(expenseAmount));
        category.removeExpense(expense);
        categoryRepository.save(category);
        // orphanRemoval = true
    }
}
