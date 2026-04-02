package com.example.auth_service.config;

import com.example.auth_service.entity.AppUser;
import com.example.auth_service.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
  @Bean
  CommandLineRunner initUsers(AppUserRepository repository, PasswordEncoder encoder) {
    return args -> {
      if (repository.findByUsername("user.nom").isEmpty()) {
        AppUser user = new AppUser();
        user.setUsername("user.nom");
        user.setPassword(encoder.encode("password"));
        user.setRole("USER");
        repository.save(user);
      }
    };
  }

}
