package ibf2022.tfipminiproject.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ibf2022.tfipminiproject.entities.Expense;
import ibf2022.tfipminiproject.entities.User;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    @Query("SELECT e FROM Expense e WHERE e.category.budget.user = :user")
    List<Expense> findAllByUser(@Param("user") User user);
}
