package org.autoriaclonebackend.auth;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.auth.dto.LoginRequest;
import org.autoriaclonebackend.auth.dto.LoginResponse;
import org.autoriaclonebackend.auth.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/register/admin")
    public ResponseEntity<String> registerManager(@RequestBody RegisterRequest request) {
        try {
            authService.register(request,"MANAGER");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Manager registered");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/premium")
    public ResponseEntity<String> buyPremium(Authentication auth) {
        try {
            authService.buyPremium(auth);
            return ResponseEntity.ok("User successfully upgraded to PREMIUM");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
