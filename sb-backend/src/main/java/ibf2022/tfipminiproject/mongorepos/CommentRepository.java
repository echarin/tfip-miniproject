package ibf2022.tfipminiproject.mongorepos;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ibf2022.tfipminiproject.mongoentities.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByExpenseId(UUID expenseId, Pageable pageable);
}