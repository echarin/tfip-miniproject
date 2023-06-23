package ibf2022.tfipminiproject.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.tfipminiproject.dtos.CommentDTO;
import ibf2022.tfipminiproject.dtos.ResponseDTO;
import ibf2022.tfipminiproject.services.AuthenticationService;
import ibf2022.tfipminiproject.services.CommentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    
    private final AuthenticationService authService;
    private final CommentService commentService;

    @GetMapping("/{userId}/{expenseId}/comments")
    public ResponseEntity<Page<CommentDTO>> getAllCommentsByExpenseId(
        @PathVariable("userId") UUID userId,
        @PathVariable("expenseId") UUID expenseId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CommentDTO> commentsResponse = commentService.findAllByExpenseId(expenseId, pageable);
        return ResponseEntity.ok(commentsResponse);
    }

    @PostMapping("/{userId}/{expenseId}/comments")
    public ResponseEntity<CommentDTO> createComment(
        @PathVariable("userId") UUID userId,
        @PathVariable("expenseId") UUID expenseId,
        @RequestBody CommentDTO comment,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        CommentDTO commentResponse = commentService.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    public ResponseEntity<ResponseDTO> deleteComment(
        @PathVariable("userId") UUID userId,
        @PathVariable("commentId") String commentId,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        commentService.delete(userId, commentId);
        ResponseDTO response = new ResponseDTO("Comment successfully deleted");
        return ResponseEntity.ok(response);
    }
}
