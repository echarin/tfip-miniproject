package ibf2022.tfipminiproject.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.User;


public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    Optional<Budget> findByUser(User user);

    @Query("""
    SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
    FROM Budget b
    WHERE b.user = :user AND b.id = :budgetId
        """)
    boolean existsByUserAndId(User user, UUID budgetId);
}
