package ibf2022.tfipminiproject.services;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.BudgetMapper;
import ibf2022.tfipminiproject.repositories.BudgetRepository;
import ibf2022.tfipminiproject.repositories.UserRepository;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Budget budget = budgetMapper.budgetDTOToBudget(budgetDTO);
        user.setBudget(budget);
        userRepository.save(user);

        // Fetch the saved budget from the database
        Budget savedBudget = budgetRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        return budgetMapper.budgetToBudgetDTO(savedBudget);
    }

    @Transactional
    public void delete(UUID budgetId) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        User user = budget.getUser();
        user.setBudget(null);
        userRepository.save(user); 
        // Since we have orphan removal = true, 
        // saving the user should be enough to delete the budget
        // budgetRepository.delete(budget);
    } 
}
