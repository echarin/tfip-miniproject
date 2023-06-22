package ibf2022.tfipminiproject.services;

import static org.junit.jupiter.api.Assertions.assertSame;
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

import ibf2022.tfipminiproject.dtos.CategoryDTO;
import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.mappers.CategoryMapper;
import ibf2022.tfipminiproject.repositories.BudgetRepository;
import ibf2022.tfipminiproject.repositories.CategoryRepository;
import jakarta.persistence.EntityManager;

@SpringBootTest
public class CategoryServiceTest {
    
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
        UUID budgetId = UUID.randomUUID();
        Budget budget = new Budget();

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
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));
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

        CategoryDTO result = categoryService.save(budgetId, categoryDTO);

        assertSame(savedCategoryDTO, result);

        // Verify that all expected methods have been called
        verify(budgetRepository).findById(budgetId);
        verify(categoryMapper).categoryDTOToCategory(categoryDTO);
        verify(budgetRepository).save(budget);
        verify(categoryRepository).findById(savedCategory.getId());
        verify(categoryMapper).categoryToCategoryDTO(savedCategory);
    }
}
