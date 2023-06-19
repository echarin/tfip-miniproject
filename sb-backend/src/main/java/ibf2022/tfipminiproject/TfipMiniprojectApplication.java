package ibf2022.tfipminiproject;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.Expense;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.repositories.BudgetRepository;
import ibf2022.tfipminiproject.repositories.CategoryRepository;
import ibf2022.tfipminiproject.repositories.ExpenseRepository;
import ibf2022.tfipminiproject.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@EnableJpaAuditing
@RequiredArgsConstructor
public class TfipMiniprojectApplication implements CommandLineRunner {

	private final ExpenseRepository expenseRepository;
	private final CategoryRepository categoryRepository;
	private final BudgetRepository budgetRepository;
	private final UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(TfipMiniprojectApplication.class, args);
	}

	@Override
	@Transactional // This is required to prevent "detached entity passed to persist"
	public void run(String... args) throws Exception {
		// printUser();
		// changeUserPassword();
		// printUser();
	}

	public void createUser() {
		User user = new User();
		user.setEmail("justuser@mail.com");
		user.setPassword("XXXXXXXX");
	}

	public void createUserWithEverything() {
		Expense expense = new Expense();
		expense.setAmount(30.00);
		expense.setDate(LocalDate.now());
		expenseRepository.save(expense);

		Category category = new Category();
		category.setName("Test category");
		category.setBudgetedAmount(300.00);
		category.addExpense(expense);
		categoryRepository.save(category);

		Budget budget = new Budget();
		budget.setName("Test's budget");
		budget.setMoneyPool(500.00);
		budget.addCategory(category);
		budgetRepository.save(budget);

		User user = new User();
		user.setEmail("test7@gmail.com");
		user.setPassword("password");
		user.setBudget(budget);
		User savedUser = userRepository.save(user);

		System.out.println(savedUser);
	}

	public void printUser() {
		UUID uuid = UUID.fromString("b2344fdb-397f-4754-a46a-943845432214");
		User user = userRepository.findById(uuid).orElse(null);
		if (user != null) { System.out.println(user); }
	}

	public void changeUserPassword() {
		UUID uuid = UUID.fromString("b2344fdb-397f-4754-a46a-943845432214");
		User user = userRepository.findById(uuid).orElse(null);
		if (user != null) { 
			user.setPassword("newPassword"); 
			userRepository.save(user);
		}
	}

	// Test
}
