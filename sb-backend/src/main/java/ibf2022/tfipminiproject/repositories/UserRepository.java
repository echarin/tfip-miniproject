package ibf2022.tfipminiproject.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ibf2022.tfipminiproject.entities.User;


public interface UserRepository extends JpaRepository<User, UUID> {

    // Custom query: findBy<field_name>
    Optional<User> findByEmail(String email);
}
