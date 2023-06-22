// package ibf2022.tfipminiproject.services;

// import java.util.List;
// import java.util.UUID;

// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import ibf2022.tfipminiproject.dtos.BudgetDTO;
// import ibf2022.tfipminiproject.entities.Budget;
// import ibf2022.tfipminiproject.entities.Category;
// import ibf2022.tfipminiproject.entities.User;
// import ibf2022.tfipminiproject.exceptions.BudgetNotFoundException;
// import ibf2022.tfipminiproject.repositories.CategoryRepository;
// import ibf2022.tfipminiproject.repositories.UserRepository;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CategoryService {

//     private final UserRepository userRepository;
//     private final CategoryRepository categoryRepository;
//     private final CategoryMapper categoryMapper;
    
//     public List<CategoryDTO> getAllCategoriesByUser(UUID userId) {
//         User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//         List<Category> categories = categoryRepository.findAllByUser(user);
//         categories.
//     }

//     @Transactional
//     public BudgetDTO save(UUID userId, BudgetDTO budgetDTO) {
//         User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//         Budget budget = new Budget();
//         budget.setName(budgetDTO.getName());
//         budget.setMoneyPool(budgetDTO.getMoneyPool());
//         user.setBudget(budget);
//         userRepository.save(user);

//         // Fetch the saved budget from the database
//         Budget savedBudget = budgetRepository.findByUser(user).orElseThrow(() -> new BudgetNotFoundException("Budget not found"));
//         return budgetMapper.budgetToBudgetDTO(savedBudget);
//     }

//     @Transactional
//     public void delete(UUID budgetId) {
//         Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new BudgetNotFoundException("Budget not found"));
//         User user = budget.getUser();
//         user.setBudget(null);
//         userRepository.save(user); 
//         // Since we have orphan removal = true, 
//         // saving the user should be enough to delete the budget
//         // budgetRepository.delete(budget);
//     } 
// }