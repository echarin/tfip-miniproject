package ibf2022.tfipminiproject.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.tfipminiproject.dtos.AuthenticationRequest;
import ibf2022.tfipminiproject.dtos.AuthenticationResponse;
import ibf2022.tfipminiproject.entities.Role;
import ibf2022.tfipminiproject.entities.User;
import ibf2022.tfipminiproject.exceptions.EmailAlreadyExistsException;
import ibf2022.tfipminiproject.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthenticationResponse register(AuthenticationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Username by that email already exists");
        }
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
        userRepository.save(user);
        return generateAuthenticationResponse(user);
    }

    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword()
            ));
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return generateAuthenticationResponse(user);
    }

    private AuthenticationResponse generateAuthenticationResponse(User user) {
        String jwtToken = jwtService.generateToken(user);
        Date expiresAt = jwtService.extractClaim(jwtToken, Claims::getExpiration);
        long expiresAtMillis = expiresAt.getTime();
        return AuthenticationResponse.builder()
            .userId(user.getId())
            .token(jwtToken)
            .expiresAt(expiresAtMillis)
            .build();
    }

    public boolean doesUserIdMatch(UUID userId, Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userDetails.getUsername().equals(userId.toString());
    }
}
