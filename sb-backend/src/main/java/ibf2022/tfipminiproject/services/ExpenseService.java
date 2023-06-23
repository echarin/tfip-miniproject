package ibf2022.tfipminiproject.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final EntityManager entityManager;
    
    public List<ExpenseDTO> getAllExpensesByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Expense> expenses = expenseRepository.findAllByUser(user);
        return expenses.stream()
                .map(expenseMapper::expenseToExpenseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDTO save(UUID categoryId, ExpenseDTO expenseDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Expense expense = expenseMapper.expenseDTOToExpense(expenseDTO);

        category.addExpense(expense);
        categoryRepository.save(category);
        entityManager.flush();
        entityManager.refresh(category);

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
        Expense expense = expenseRepository.findById(expenseId)
            .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        boolean userOwnsExpense = expenseRepository.existsByUserAndId(user, expenseId);

        if (!userOwnsExpense) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }
        
        Category category = expense.getCategory();
        category.removeExpense(expense);
        categoryRepository.save(category);
        // Since we have orphan removal = true, 
        // saving the budget should be enough to delete the category
    } 
}
