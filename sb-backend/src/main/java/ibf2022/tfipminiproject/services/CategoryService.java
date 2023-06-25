package ibf2022.tfipminiproject.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.CategoryDTO;
import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.exceptions.EntityProcessingException;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.CategoryMapper;
import ibf2022.tfipminiproject.repositories.BudgetRepository;
import ibf2022.tfipminiproject.repositories.CategoryRepository;
import ibf2022.tfipminiproject.repositories.UserRepository;
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
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Category> categories = categoryRepository.findAllByUser(user);
        return categories.stream()
                .map(categoryMapper::categoryToCategoryDTO)
                .collect(Collectors.toList());
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

        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);
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

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Budget budget = category.getBudget();
        budget.removeCategory(category);
        budgetRepository.save(budget);
        // orphanRemoval = true
    } 
}