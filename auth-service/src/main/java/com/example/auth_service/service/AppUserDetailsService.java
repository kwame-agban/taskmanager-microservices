package com.example.auth_service.service;

import com.example.auth_service.entity.AppUser;
import com.example.auth_service.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
  private final AppUserRepository repository;

  public AppUserDetailsService(AppUserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser user = repository.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

    return User.withUsername(user.getUsername())
      .password(user.getPassword())
      .roles(user.getRole())
      .build();
  }

}
