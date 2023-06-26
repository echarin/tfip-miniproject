package ibf2022.tfipminiproject.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
import jakarta.persistence.EntityManager;

@SpringBootTest
public class CategoryServiceTest {

    @Spy
    private UserRepository userRepository;
    
    @Spy
    private BudgetRepository budgetRepository;

    @Spy
    private CategoryRepository categoryRepository;

    @Spy
    private CategoryMapper categoryMapper;

    @Spy
    private EntityManager entityManager;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = new Budget();
        budget.setUser(User.builder().id(budgetId).build());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setBudgetedAmount(new BigDecimal("1000.00"));
        categoryDTO.setCreatedAt(LocalDateTime.now());
        
        Category savedCategory = new Category();
        savedCategory.setName("Test Category");
        savedCategory.setBudgetedAmount(new BigDecimal("1000.00"));
        savedCategory.setBudget(budget);

        CategoryDTO savedCategoryDTO = new CategoryDTO();
        savedCategoryDTO.setName("Test Category");
        savedCategoryDTO.setBudgetedAmount(new BigDecimal("1000.00"));
        savedCategoryDTO.setCreatedAt(LocalDateTime.now());

        // The following 'when' methods are called during test setup, before the test is executed
        when(userRepository.findById(userId)).thenReturn(Optional.of(budget.getUser()));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByUserAndId(budget.getUser(), budgetId)).thenReturn(true);
        when(categoryMapper.categoryDTOToCategory(categoryDTO)).thenReturn(savedCategory);

        // Use doAnswer to setup a Mockito behavior that should occur during the test execution
        doAnswer(invocation -> {
            UUID categoryId = UUID.randomUUID();
            savedCategory.setId(categoryId);
            // This 'when' method is inside doAnswer because it needs to use a value (categoryId) that is only available during test execution
            when(categoryRepository.findById(savedCategory.getId())).thenReturn(Optional.of(savedCategory));
            return null;
        }).when(budgetRepository).save(budget);

        when(categoryMapper.categoryToCategoryDTO(savedCategory)).thenReturn(savedCategoryDTO);

        CategoryDTO result = categoryService.save(userId, budgetId, categoryDTO);

        assertSame(savedCategoryDTO, result);

        // Verify that all expected methods have been called
        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(budget.getUser(), budgetId);
        verify(categoryMapper).categoryDTOToCategory(categoryDTO);
        verify(budgetRepository).save(budget);
        verify(categoryRepository).findById(savedCategory.getId());
        verify(categoryMapper).categoryToCategoryDTO(savedCategory);
    }

    @Test
    public void testSaveUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            categoryService.save(userId, budgetId, categoryDTO);
        });

        verify(userRepository).findById(userId);
    }

    @Test
    public void testSaveBudgetNotFound() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.save(userId, budgetId, categoryDTO);
        });

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
    }

    @Test
    public void testSaveUserDoesNotOwnBudget() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        CategoryDTO categoryDTO = new CategoryDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(new Budget()));
        when(budgetRepository.existsByUserAndId(any(), any())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> {
            categoryService.save(userId, budgetId, categoryDTO);
        });

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(any(), any());
    }

    @Test
    public void testSaveCategoryIdNotGenerated() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = new Budget();
        budget.setUser(User.builder().id(budgetId).build());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setBudgetedAmount(new BigDecimal("1000.00"));
        categoryDTO.setCreatedAt(LocalDateTime.now());
            
        Category savedCategory = new Category();
        savedCategory.setName("Test Category");
        savedCategory.setBudgetedAmount(new BigDecimal("1000.00"));
        savedCategory.setBudget(budget);

        when(userRepository.findById(userId)).thenReturn(Optional.of(budget.getUser()));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByUserAndId(budget.getUser(), budgetId)).thenReturn(true);
        when(categoryMapper.categoryDTOToCategory(categoryDTO)).thenReturn(savedCategory);
        when(budgetRepository.save(budget)).thenReturn(budget);

        assertThrows(EntityProcessingException.class, () -> {
            categoryService.save(userId, budgetId, categoryDTO);
        });

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(budget.getUser(), budgetId);
        verify(categoryMapper).categoryDTOToCategory(categoryDTO);
        verify(budgetRepository).save(budget);
    }

    @Test
    public void testSave_categoryNotFoundAfterSave() {
        UUID userId = UUID.randomUUID();
        UUID budgetId = UUID.randomUUID();
        Budget budget = new Budget();
        budget.setUser(User.builder().id(budgetId).build());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Test Category");
        categoryDTO.setBudgetedAmount(new BigDecimal("1000.00"));
        categoryDTO.setCreatedAt(LocalDateTime.now());
            
        Category savedCategory = new Category();
        savedCategory.setName("Test Category");
        savedCategory.setBudgetedAmount(new BigDecimal("1000.00"));
        savedCategory.setBudget(budget);
        savedCategory.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(budget.getUser()));
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
        when(budgetRepository.existsByUserAndId(budget.getUser(), budgetId)).thenReturn(true);
        when(categoryMapper.categoryDTOToCategory(categoryDTO)).thenReturn(savedCategory);
        when(budgetRepository.save(budget)).thenReturn(budget);
        when(categoryRepository.findById(savedCategory.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.save(userId, budgetId, categoryDTO);
        });

        verify(userRepository).findById(userId);
        verify(budgetRepository).findById(budgetId);
        verify(budgetRepository).existsByUserAndId(budget.getUser(), budgetId);
        verify(categoryMapper).categoryDTOToCategory(categoryDTO);
        verify(budgetRepository).save(budget);
        verify(categoryRepository).findById(savedCategory.getId());
    }
}