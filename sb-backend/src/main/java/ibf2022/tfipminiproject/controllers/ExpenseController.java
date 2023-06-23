package ibf2022.tfipminiproject.controllers;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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

import ibf2022.tfipminiproject.dtos.ExpenseDTO;
import ibf2022.tfipminiproject.dtos.ResponseDTO;
import ibf2022.tfipminiproject.services.AuthenticationService;
import ibf2022.tfipminiproject.services.ExpenseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final AuthenticationService authService;
    private final ExpenseService expenseService;

    @GetMapping("/{userId}/expenses")
    public ResponseEntity<Page<ExpenseDTO>> getAllExpensesByUser(
        @PathVariable("userId") UUID userId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "from", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(value = "to", required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expensesResponse = expenseService.getAllExpensesByUser(userId, from, to, pageable);
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

        ExpenseDTO expenseResponse = expenseService.save(userId, categoryId, expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponse);
    }

    @DeleteMapping("/{userId}/expenses/{expenseId}")
    public ResponseEntity<ResponseDTO> deleteExpense(
        @PathVariable("userId") UUID userId,
        @PathVariable("expenseId") UUID expenseId,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        expenseService.delete(userId, expenseId);
        ResponseDTO response = new ResponseDTO("Expense successfully deleted");
        return ResponseEntity.ok(response);
    }
}
