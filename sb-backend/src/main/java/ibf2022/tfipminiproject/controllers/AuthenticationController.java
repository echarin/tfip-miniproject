package ibf2022.tfipminiproject.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.tfipminiproject.dtos.AuthenticationRequest;
import ibf2022.tfipminiproject.dtos.AuthenticationResponse;
import ibf2022.tfipminiproject.exceptions.EmailAlreadyExistsException;
import ibf2022.tfipminiproject.services.AuthenticationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) throws EmailAlreadyExistsException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws AuthenticationException {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/test")
    public ResponseEntity<AuthenticationResponse> test() {
        return ResponseEntity.ok(AuthenticationResponse.builder().token("test").build());
    }
}
