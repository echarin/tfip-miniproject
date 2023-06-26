package ibf2022.tfipminiproject.services;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.BudgetMapper;
import ibf2022.tfipminiproject.sqlentities.Budget;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.BudgetRepository;
import ibf2022.tfipminiproject.sqlrepositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;
    
    public BudgetDTO findByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Budget budget = budgetRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return budgetMapper.budgetToBudgetDTO(budget);
    }

    @Transactional
    public BudgetDTO save(UUID userId, BudgetDTO budgetDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Budget budget = budgetMapper.budgetDTOToBudget(budgetDTO);
        user.setBudget(budget);
        userRepository.save(user);

        // Fetch the saved budget from the database
        Budget savedBudget = budgetRepository.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return budgetMapper.budgetToBudgetDTO(savedBudget);
    }

    @Transactional
    public void delete(UUID userId, UUID budgetId) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Then check if budget exists
        if (budgetRepository.findById(budgetId).isEmpty()) {
            throw new ResourceNotFoundException("Budget not found");
        }

        boolean userOwnsBudget = budgetRepository.existsByUserAndId(user, budgetId);

        if (!userOwnsBudget) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }
        
        user.setBudget(null);
        userRepository.save(user); 
        // orphanRemoval = true
    } 
}
