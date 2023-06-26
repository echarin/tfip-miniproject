package ibf2022.tfipminiproject.repositories;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import ibf2022.tfipminiproject.sqlentities.Budget;
import ibf2022.tfipminiproject.sqlentities.Category;
import ibf2022.tfipminiproject.sqlentities.Expense;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.ExpenseRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ExpenseRepositoryTest {
    
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private TestEntityManager entityManager;

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

        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setDate(LocalDate.now());

        user.setBudget(budget);
        budget.addCategory(category);
        category.addExpense(expense);

        entityManager.persist(user);

        boolean exists = expenseRepository.existsByUserAndId(user, expense.getId());

        assertThat(exists).isTrue();
    }

    @Test
    public void testFindAllByUserAndDateBetween() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("password");

        Budget budget = new Budget();
        budget.setName("Test Budget");
        budget.setMoneyPool(BigDecimal.valueOf(1000));

        Category category = new Category();
        category.setName("Test Category");
        category.setBudgetedAmount(BigDecimal.valueOf(100));

        Expense expense1 = new Expense();
        expense1.setAmount(BigDecimal.valueOf(100));
        expense1.setDate(LocalDate.now());

        Expense expense2 = new Expense();
        expense2.setAmount(BigDecimal.valueOf(100));
        expense2.setDate(LocalDate.now().minusDays(1));

        user.setBudget(budget);
        budget.addCategory(category);
        category.addExpense(expense1);
        category.addExpense(expense2);

        entityManager.persist(user);

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Expense> page = expenseRepository.findAllByUserAndDateBetween(
                user,
                LocalDate.now().minusDays(2),
                LocalDate.now(),
                pageRequest
        );

        assertThat(page.getTotalElements()).isEqualTo(2);
    }
}
