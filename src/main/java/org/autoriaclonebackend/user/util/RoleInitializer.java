package org.autoriaclonebackend.user.util;

import lombok.RequiredArgsConstructor;
import org.autoriaclonebackend.user.model.Role;
import org.autoriaclonebackend.user.repository.RoleRepository;
import org.autoriaclonebackend.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RoleInitializer.class);

    @Override
    public void run(ApplicationArguments args) {
        if (roleRepository.findAll().isEmpty()) {
            roleRepository.save(Role.builder().name("ADMIN").build());
            roleRepository.save(Role.builder().name("SELLER").build());
            roleRepository.save(Role.builder().name("PREMIUM").build());
            roleRepository.save(Role.builder().name("MANAGER").build());

            log.info("Roles created");
        } else {
            log.info("Roles already exists.");
        }
        log.info(
                userService.registerUserAdmin("testadmin@gmail.com", "testAdmin","meow")
        );
    }
}
