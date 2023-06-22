// package ibf2022.tfipminiproject.controllers;

// import java.util.UUID;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;

// import ibf2022.tfipminiproject.services.AuthenticationService;
// import lombok.RequiredArgsConstructor;

// @Controller
// @RequestMapping("/api/v1")
// @RequiredArgsConstructor
// public class CategoryController {
    
//     private final AuthenticationService authService;
//     private final CategoryService categoryService;

//     @GetMapping("/{userId}/categories")
//     public ResponseEntity<List<CategoryDTO>> getAllCategoriesByUser(
//         @PathVariable("userId") UUID userId,
//         Authentication auth
//     ) {
//         if (!authService.doesUserIdMatch(userId, auth)) {
//             throw new AccessDeniedException("You do not have access to this resource.");
//         }

//         List<CategoryDTO> categoriesResponse = categoryService.getAllCategoriesByUser(userId);

//         return ResponseEntity.ok(categoriesResponse);
//     }
// }
