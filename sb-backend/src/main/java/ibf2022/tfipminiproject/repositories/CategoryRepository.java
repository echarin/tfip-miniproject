package ibf2022.tfipminiproject.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ibf2022.tfipminiproject.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
}
