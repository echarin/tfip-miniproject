package ibf2022.tfipminiproject.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EntityManager entityManager;
    
    public List<CategoryDTO> getAllCategoriesByUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Category> categories = categoryRepository.findAllByUser(user);
        return categories.stream()
                .map(categoryMapper::categoryToCategoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO save(UUID budgetId, CategoryDTO categoryDTO) {
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        Category category = categoryMapper.categoryDTOToCategory(categoryDTO);

        budget.addCategory(category);
        budgetRepository.save(budget);
        entityManager.flush();
        entityManager.refresh(budget);

        // Hibernate should also update the ID in the category entity
        UUID categoryId = category.getId();
        if (categoryId == null) {
            throw new EntityProcessingException("Category ID not generated after save");
        }

        Category savedCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.categoryToCategoryDTO(savedCategory);
    }

    @Transactional
    public void delete(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Budget budget = category.getBudget();
        budget.removeCategory(category);
        budgetRepository.save(budget);
        // Since we have orphan removal = true, 
        // saving the budget should be enough to delete the category
    } 
}