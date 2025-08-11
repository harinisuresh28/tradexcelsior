package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserDto {

  @NotBlank(message = "First name cannot be blank.")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank.")
  private String lastName;

  @NotBlank(message = "Occupation cannot be blank.")
  private String occupation;

  @NotBlank(message = "Mobile number cannot be blank.")
  @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits.")
  @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain digits only.")
  private String mobileNumber;

  @NotBlank(message = "Email cannot be blank.")
  @Email(message = "Please provide a valid email address.")
  private String email;

  @Size(min = 8, message = "Password must be at least 8 characters long.")
  private String password;

  @NotBlank(message = "Role cannot be blank.")
  private String role;

  @NotBlank(message = "License cannot be blank.")
  private String licence;

  @NotBlank(message = "Address cannot be blank.")
  private String address;

  @NotNull(message = "Subscription duration cannot be null.")
  @Min(value = 1, message = "Subscription duration must be at least 1 year.")
  @Max(value = 20, message = "Subscription duration cannot exceed 20 years.")
  private int subscriptionDuration;
}
