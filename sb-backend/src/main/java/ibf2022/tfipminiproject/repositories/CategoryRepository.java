package ibf2022.tfipminiproject.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ibf2022.tfipminiproject.entities.Category;
import ibf2022.tfipminiproject.entities.User;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE c.budget.user = :user")
    List<Category> findAllByUser(@Param("user") User user);
}
