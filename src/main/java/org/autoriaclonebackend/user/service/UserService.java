package org.autoriaclonebackend.user.service;


import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.car.repository.CarAdRepository;
import org.autoriaclonebackend.user.model.Role;
import org.autoriaclonebackend.user.model.User;
import org.autoriaclonebackend.user.repository.RoleRepository;
import org.autoriaclonebackend.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarAdRepository carAdRepository;

    public String registerUserAdmin(String email,String fullName, String password) {
        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role ADMIN is undefined"));



        List<User> users = userRepository.findAllByRoles(Set.of(role));

        if (users.isEmpty() == true) {
            User user = User.builder()
                .email(email)
                .fullName(fullName)
                .password(passwordEncoder.encode(password))
                .roles(Collections.singleton(role))
                .build();
            userRepository.save(user);
        }
        return "User with ADMIN are already exists";

    }

    public void banUser(Long id,Authentication auth) {
        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("MANAGER"))) {

        }

        var userById = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id: " + id + " not found"));

        userRepository.save(userById);
    }
}