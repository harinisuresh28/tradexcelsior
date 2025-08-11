package com.tp.tradexcelsior.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends CommonEntity implements UserDetails {

  @Id
  private String id;
  private String firstName;
  private String lastName;
  private String occupation;

  @Indexed(unique = true)
  private String mobileNumber;

  @Indexed(unique = true)
  private String email;

  private String password;
  private String role;
  private String licence;
  private String address;
  private LocalDate subscriptionStartDate;
  private LocalDate subscriptionEndDate;
  private int subscriptionDuration;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Assign authorities based on the role of the user
    if ("ADMIN".equals(this.role)) {
      return List.of(() -> "ROLE_ADMIN");
    } else {
      return List.of(() -> "ROLE_USER");
    }
  }


  @Override
  public String getUsername() {
    return this.email;
  }

}
