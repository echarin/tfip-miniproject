package ibf2022.tfipminiproject.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.dtos.ResponseDTO;
import ibf2022.tfipminiproject.exceptions.ResourceNotFoundException;
import ibf2022.tfipminiproject.services.AuthenticationService;
import ibf2022.tfipminiproject.services.BudgetService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BudgetController {

    private final AuthenticationService authService;
    private final BudgetService budgetService;
    
    @GetMapping("/{userId}/budget")
    public ResponseEntity<BudgetDTO> getBudget(
        @PathVariable("userId") UUID userId,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        BudgetDTO budgetResponse = budgetService.findByUser(userId);
        return ResponseEntity.ok(budgetResponse);
    }

    @PostMapping("/{userId}/budget")
    public ResponseEntity<BudgetDTO> createBudget(
        @PathVariable("userId") UUID userId,
        @RequestBody BudgetDTO budgetDTO,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        BudgetDTO budgetResponse = budgetService.save(userId, budgetDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetResponse);
    }

    @PutMapping("/{userId}/budget")
    public ResponseEntity<BudgetDTO> updateBudget(
        @PathVariable("userId") UUID userId,
        @RequestBody BudgetDTO budgetDTO,
        Authentication auth
    ) {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        BudgetDTO budgetResponse = budgetService.save(userId, budgetDTO);
        return ResponseEntity.ok(budgetResponse);
    }

    @DeleteMapping("/{userId}/budget/{budgetId}")
    public ResponseEntity<ResponseDTO> deleteBudget(
        @PathVariable("userId") UUID userId,
        @PathVariable("budgetId") UUID budgetId,
        Authentication auth
    ) throws ResourceNotFoundException {
        if (!authService.doesUserIdMatch(userId, auth)) {
            throw new AccessDeniedException("You do not have access to this resource.");
        }

        budgetService.delete(userId, budgetId);
        ResponseDTO response = new ResponseDTO("Budget successfully deleted");
        return ResponseEntity.ok(response);
    }
}
