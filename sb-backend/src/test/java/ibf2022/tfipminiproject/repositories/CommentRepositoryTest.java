package ibf2022.tfipminiproject.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ibf2022.tfipminiproject.mongoentities.Comment;
import ibf2022.tfipminiproject.mongorepos.CommentRepository;

@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    // Currently does not work in GitHub Actions;
    // @Test
    // public void testFindByExpenseId() {
    //     Comment comment = new Comment();
    //     comment.setText("Test Comment");
    //     UUID expenseId = UUID.randomUUID();
    //     comment.setExpenseId(expenseId);

    //     commentRepository.save(comment);

    //     Pageable pageable = PageRequest.of(0, 10);
    //     Page<Comment> found = commentRepository.findByExpenseId(expenseId, pageable);

    //     List<Comment> comments = found.getContent();
    //     assertEquals(1, comments.size());
    //     assertEquals(comment.getText(), comments.get(0).getText());
    //     assertEquals(comment.getExpenseId(), comments.get(0).getExpenseId());
    // }
}
