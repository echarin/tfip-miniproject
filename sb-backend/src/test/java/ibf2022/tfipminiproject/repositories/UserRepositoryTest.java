package ibf2022.tfipminiproject.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ibf2022.tfipminiproject.entities.Role;
import ibf2022.tfipminiproject.entities.User;

@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByEmail() {
        User user = User.builder()
            .email("test@email.com")
            .password("password")
            .role(Role.USER)
            .build();
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail(user.getEmail());

        assertTrue(found.isPresent());
        assertEquals(user.getEmail(), found.get().getEmail());
    }
}
