package org.autoriaclonebackend.user.repository;


import org.autoriaclonebackend.user.model.Role;
import org.autoriaclonebackend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findAllByRoles(Set<Role> roles);
}