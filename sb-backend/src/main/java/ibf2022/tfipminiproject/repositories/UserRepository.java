package ibf2022.tfipminiproject.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ibf2022.tfipminiproject.entities.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    // To prevent this particular DELETE operation
    // @Override
    // @RestResource(exported = false)
    // void deleteById(UUID id);
}
