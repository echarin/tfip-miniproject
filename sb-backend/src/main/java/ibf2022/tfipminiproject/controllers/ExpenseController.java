package ibf2022.tfipminiproject.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ibf2022.tfipminiproject.dtos.ExpenseDTO;
import ibf2022.tfipminiproject.dtos.ResponseDTO;
import ibf2022.tfipminiproject.services.AuthenticationService;
import ibf2022.tfipminiproject.services.ExpenseService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final AuthenticationService authService;
    private final ExpenseService expenseService;

    @GetMapping("/{userId}/expenses")
    public ResponseEntity<List<ExpenseDTO>> getAllExpensesByUser(
        @PathVariable("userId") UUID userId,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        List<ExpenseDTO> expensesResponse = expenseService.getAllExpensesByUser(userId);
        return ResponseEntity.ok(expensesResponse);
    }

    @PostMapping("/{userId}/{categoryId}/expenses")
    public ResponseEntity<ExpenseDTO> createExpense(
        @PathVariable("userId") UUID userId,
        @PathVariable("categoryId") UUID categoryId,
        @RequestBody ExpenseDTO expenseDTO,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        ExpenseDTO expenseResponse = expenseService.save(categoryId, expenseDTO);
        return ResponseEntity.ok(expenseResponse);
    }

    @DeleteMapping("/{userId}/expenses/{expenseId}")
    public ResponseEntity<ResponseDTO> deleteCategory(
        @PathVariable("userId") UUID userId,
        @PathVariable("expenseId") UUID expenseId,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        expenseService.delete(expenseId);
        ResponseDTO response = new ResponseDTO("Expense successfully deleted");
        return ResponseEntity.ok(response);
    }
}
