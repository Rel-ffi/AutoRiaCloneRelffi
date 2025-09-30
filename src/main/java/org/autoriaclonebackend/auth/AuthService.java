package org.autoriaclonebackend.auth;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.auth.dto.LoginRequest;
import org.autoriaclonebackend.auth.dto.LoginResponse;
import org.autoriaclonebackend.auth.dto.RegisterRequest;
import org.autoriaclonebackend.auth.security.JwtUtil;
import org.autoriaclonebackend.user.model.Role;
import org.autoriaclonebackend.user.model.User;
import org.autoriaclonebackend.user.repository.RoleRepository;
import org.autoriaclonebackend.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("A user with this email already exists.");
        }

        Role sellerRole = roleRepository.findByName("SELLER")
                .orElseThrow(() -> new RuntimeException("Role SELLER is not found"));

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(sellerRole))
                .build();

        userRepository.save(user);
    }

    public void register(RegisterRequest request, String manager) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("A user with this email already exists.");
        }

        Role managerRole = roleRepository.findByName(manager.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role SELLER is not found"));

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(managerRole))
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Incorrect email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    public void buyPremium(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));



        Role premiumRole = roleRepository.findByName("PREMIUM")
                .orElseThrow(() -> new RuntimeException("Role PREMIUM not found"));

        if (user.getRoles().contains(premiumRole)){
            throw new RuntimeException("Seller already has Premium");
        }
        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.add(premiumRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

}
