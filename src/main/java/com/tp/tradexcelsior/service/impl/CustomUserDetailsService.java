package com.tp.tradexcelsior.service.impl;


import com.tp.tradexcelsior.entity.User;
import com.tp.tradexcelsior.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepo userRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepo.findByEmailAndIsDeletedFalse(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    return user;
  }
}
