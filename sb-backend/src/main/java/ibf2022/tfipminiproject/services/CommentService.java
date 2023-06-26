package ibf2022.tfipminiproject.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import ibf2022.tfipminiproject.dtos.CommentDTO;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.mappers.CommentMapper;
import ibf2022.tfipminiproject.mongoentities.Comment;
import ibf2022.tfipminiproject.mongorepos.CommentRepository;
import ibf2022.tfipminiproject.sqlentities.User;
import ibf2022.tfipminiproject.sqlrepositories.ExpenseRepository;
import ibf2022.tfipminiproject.sqlrepositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public Page<CommentDTO> findAllByExpenseId(UUID expenseId, Pageable pageable) {
        return commentRepository.findByExpenseId(expenseId, pageable)
                .map(commentMapper::commentToCommentDTO);
    }

    public CommentDTO save(UUID userId, UUID expenseId, CommentDTO comment) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean userOwnsExpense = expenseRepository.existsByUserAndId(user, expenseId);

        if (!userOwnsExpense) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }
        
        Comment savedComment = commentRepository.save(commentMapper.commentDTOToComment(comment));
        return commentMapper.commentToCommentDTO(savedComment);
    }

    public void delete(UUID userId, String commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        UUID expenseId = comment.getExpenseId();
        boolean userOwnsExpense = expenseRepository.existsByUserAndId(user, expenseId);

        if (!userOwnsExpense) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        commentRepository.delete(comment);
    }
}
