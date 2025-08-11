package com.tp.tradexcelsior.startup;

import com.tp.tradexcelsior.dto.request.AddUserDto;
import com.tp.tradexcelsior.entity.User;
import com.tp.tradexcelsior.repo.UserRepo;
import com.tp.tradexcelsior.service.impl.UserService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserCommandLineRunner implements CommandLineRunner {

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    // Check if the admin user already exists
    String adminEmail = "admin@tradexcelsior.com";

    try {
      User existingAdmin = userRepo.findByEmailAndIsDeletedFalse(adminEmail).orElse(null);

      if (existingAdmin == null) {
        // If no admin user found, create a new admin
        createAdminUser();
      } else {
        System.out.println("Admin already exists, skipping creation.");
      }
    } catch (DataAccessException ex) {
      // This will catch any database-related exceptions (e.g., connection issues)
      System.err.println("Database error while checking admin user: " + ex.getMessage());
    } catch (Exception ex) {
      // Catch any other unexpected exceptions
      System.err.println("An unexpected error occurred: " + ex.getMessage());
    }
  }

  private void createAdminUser() {
    try {
      // Create an admin user
      User admin = new User();
      admin.setFirstName("Admin");
      admin.setLastName("Admin");
      admin.setEmail("admin@tradexcelsior.com");
      admin.setPassword(passwordEncoder.encode("admin123")); // Set the password
      admin.setRole("ADMIN"); // Assign the role as ADMIN
      admin.setMobileNumber("1234567890");
      admin.setOccupation("Administrator");
      admin.setAddress("Admin address");
      admin.setSubscriptionDuration(1);


      // Save the admin to the database
      userRepo.save(admin);
      System.out.println("Admin user added to the database.");
    } catch (DataAccessException ex) {
      // This will catch any database-related exceptions (e.g., connection issues)
      System.err.println("Database error while creating admin user: " + ex.getMessage());
    } catch (Exception ex) {
      // Catch any other unexpected exceptions
      System.err.println("An unexpected error occurred while creating admin: " + ex.getMessage());
    }
  }
}
