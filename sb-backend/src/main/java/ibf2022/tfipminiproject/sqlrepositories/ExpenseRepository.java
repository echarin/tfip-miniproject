package ibf2022.tfipminiproject.sqlrepositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ibf2022.tfipminiproject.sqlentities.Category;
import ibf2022.tfipminiproject.sqlentities.Expense;
import ibf2022.tfipminiproject.sqlentities.User;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    @Query("""
        SELECT e FROM Expense e 
        WHERE e.category.budget.user = :user 
        AND (e.date >= :from OR :from is null) 
        AND (e.date <= :to OR :to is null)
        """)
    Page<Expense> findAllByUserAndDateBetween(
        @Param("user") User user, 
        @Param("from") LocalDate from, 
        @Param("to") LocalDate to, 
        Pageable pageable
    );

    @Query("SELECT e FROM Expense e WHERE e.category = :category")
    List<Expense> findAllByCategory(@Param("category") Category category);

    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END 
        FROM Expense e 
        WHERE e.category.budget.user = :user AND e.id = :expenseId
        """)
    boolean existsByUserAndId(@Param("user") User user, @Param("expenseId") UUID expenseId);
}
