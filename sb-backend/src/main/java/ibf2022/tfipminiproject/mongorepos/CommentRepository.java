package ibf2022.tfipminiproject.mongorepos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

import ibf2022.tfipminiproject.mongoentities.Comment;


public interface CommentRepository extends MongoRepository<Comment, String> {
    // Page<Comment> findByExpenseId(UUID expenseId, Pageable pageable);
    List<Comment> findByExpenseId(UUID expenseId);
}
