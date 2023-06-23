package ibf2022.tfipminiproject.repositories;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ibf2022.tfipminiproject.entities.Expense;
import ibf2022.tfipminiproject.entities.User;

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

    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END 
        FROM Expense e 
        WHERE e.category.budget.user = :user AND e.id = :expenseId
        """)
    boolean existsByUserAndId(@Param("user") User user, @Param("expenseId") UUID expenseId);
}
