package ibf2022.tfipminiproject.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.CategoryDTO;
import ibf2022.tfipminiproject.exceptions.EntityProcessingException;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.CategoryMapper;
import ibf2022.tfipminiproject.sqlentities.Budget;
import ibf2022.tfipminiproject.sqlentities.Category;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.BudgetRepository;
import ibf2022.tfipminiproject.sqlrepositories.CategoryRepository;
import ibf2022.tfipminiproject.sqlrepositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    // private Logger logger = Logger.getLogger(CategoryService.class.getName());
    
    public List<CategoryDTO> getAllCategoriesByUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Category> categories = categoryRepository.findAllByUser(user);
        return categories.stream()
                .map(categoryMapper::categoryToCategoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(UUID userId, UUID categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean userOwnsCategory = categoryRepository.existsByUserAndId(user, categoryId);

        if (!userOwnsCategory) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return categoryMapper.categoryToCategoryDTO(category);
    }

    @Transactional
    public CategoryDTO save(UUID userId, UUID budgetId, CategoryDTO categoryDTO) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        boolean userOwnsBudget = budgetRepository.existsByUserAndId(user, budgetId);

        if (!userOwnsBudget) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        // Business logic: Adding a category with a budgeted amount will subtract from budget pool
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
        BigDecimal categoryAmount = category.getBudgetedAmount();
        BigDecimal budgetAmount = budget.getMoneyPool();

        if (categoryAmount == null || budgetAmount == null) {
            throw new EntityProcessingException("Budgeted amounts were not set");
        }

        budget.setMoneyPool(budgetAmount.subtract(categoryAmount));
        budget.addCategory(category);
        budgetRepository.save(budget);

        Category savedCategory = budget.getCategories().get(budget.getCategories().size() - 1);
        UUID categoryId = savedCategory.getId();
        if (categoryId == null) {
            throw new EntityProcessingException("Category ID not generated after save");
        }

        return categoryMapper.categoryToCategoryDTO(savedCategory);
    }

    @Transactional
    public void delete(UUID userId, UUID categoryId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean userOwnsCategory = categoryRepository.existsByUserAndId(user, categoryId);

        if (!userOwnsCategory) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        // Business logic: Deleting a category with a budgeted amount will return it to budget pool
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Budget budget = category.getBudget();
        BigDecimal categoryAmount = category.getBudgetedAmount();
        BigDecimal budgetAmount = budget.getMoneyPool();

        if (categoryAmount == null || budgetAmount == null) {
            throw new EntityProcessingException("Budgeted amounts were not set");
        }

        budget.setMoneyPool(budgetAmount.add(categoryAmount));
        budget.removeCategory(category);
        budgetRepository.save(budget);
        // orphanRemoval = true
    }
}