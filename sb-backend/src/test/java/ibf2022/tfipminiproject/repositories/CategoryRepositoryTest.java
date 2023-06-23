package ibf2022.tfipminiproject.repositories;

import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testFindAllByUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setMoneyPool(BigDecimal.valueOf(1000));

        Category category = new Category();
        category.setName("Test Category");
        category.setBudgetedAmount(BigDecimal.valueOf(100));

        user.setBudget(budget);
        budget.addCategory(category);

        entityManager.persist(user);

        List<Category> categories = categoryRepository.findAllByUser(user);

        assertThat(categories.size()).isEqualTo(1);
        assertThat(categories.get(0).getName()).isEqualTo(category.getName());
    }

    @Test
    public void testExistsByUserAndId() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setMoneyPool(BigDecimal.valueOf(1000));

        Category category = new Category();
        category.setName("Test Category");
        category.setBudgetedAmount(BigDecimal.valueOf(100));

        user.setBudget(budget);
        budget.addCategory(category);

        entityManager.persist(user);

        boolean exists = categoryRepository.existsByUserAndId(user, category.getId());

        assertThat(exists).isTrue();
    }
}
