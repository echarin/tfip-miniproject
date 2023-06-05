package ibf2022.tfipminiproject;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.CategoryGroup;
import ibf2022.tfipminiproject.entities.Expense;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.repositories.BudgetRepository;
import ibf2022.tfipminiproject.repositories.CategoryGroupRepository;
import ibf2022.tfipminiproject.repositories.CategoryRepository;
import ibf2022.tfipminiproject.repositories.ExpenseRepository;
import ibf2022.tfipminiproject.repositories.UserRepository;

@SpringBootApplication
public class TfipMiniprojectApplication implements CommandLineRunner {

	@Autowired
	private ExpenseRepository expenseRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CategoryGroupRepository categoryGroupRepository;

	@Autowired
	private BudgetRepository budgetRepository;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(TfipMiniprojectApplication.class, args);
	}

	@Override
	@Transactional // This is required to prevent "detached entity passed to persist"
	public void run(String... args) throws Exception {
		// createUser();
	}

	public void createUser() {
		Expense expense = new Expense();
		expense.setAmount(30.00);
		expense.setDate(LocalDate.now());
		expenseRepository.save(expense);

		Category category = new Category();
		category.setName("Test category");
		category.setBudgetedAmount(300.00);
		category.addExpense(expense);
		categoryRepository.save(category);

		CategoryGroup categoryGroup = new CategoryGroup();
		categoryGroup.setName("Test category group");
		categoryGroup.addCategory(category);
		categoryGroupRepository.save(categoryGroup);

		Budget budget = new Budget();
		budget.setName("Test's budget");
		budget.setMoneyPool(500.00);
		budget.addCategoryGroup(categoryGroup);
		budgetRepository.save(budget);

		User user = new User();
		user.setEmail("test7@gmail.com");
		user.setPassword("password");
		user.setBudget(budget);
		User savedUser = userRepository.save(user);

		System.out.println(savedUser);
	}
}
