package ibf2022.tfipminiproject.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ibf2022.tfipminiproject.entities.Budget;
import ibf2022.tfipminiproject.entities.User;


public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    Optional<Budget> findByUser(User user);   
}
