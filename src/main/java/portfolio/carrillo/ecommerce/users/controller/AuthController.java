package portfolio.carrillo.ecommerce.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import portfolio.carrillo.ecommerce.security.jwt.JwtService;
import portfolio.carrillo.ecommerce.security.model.Role;
import portfolio.carrillo.ecommerce.users.dto.LoginRequest;
import portfolio.carrillo.ecommerce.users.dto.RegisterRequest;
import portfolio.carrillo.ecommerce.users.model.User;
import portfolio.carrillo.ecommerce.users.repository.UserRepository;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email ya registrado");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .lastName(req.getLastName())
                .role(Role.ROLE_USER) // 👈 default
                .build();
        userRepo.save(user);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepo.findByEmail(req.getEmail()).orElseThrow();

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRole().name())
                        .build()
        );

        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole().name()));
    }

}
