package ibf2022.tfipminiproject.sqlrepositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ibf2022.tfipminiproject.sqlentities.Category;
import ibf2022.tfipminiproject.sqlentities.User;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE c.budget.user = :user")
    List<Category> findAllByUser(@Param("user") User user);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Category c
        WHERE c.budget.user = :user AND c.id = :categoryId
            """)
    boolean existsByUserAndId(User user, UUID categoryId);
}
