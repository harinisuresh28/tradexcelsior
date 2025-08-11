package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportRequestDto {

  @NotBlank(message = "Name cannot be blank.")
  private String name;

  @NotBlank(message = "Email cannot be blank.")
  @Email(message = "Please provide a valid email address.")
  private String email;

  @NotBlank(message = "Mobile number cannot be blank.")
  @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits.")
  @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must contain digits only.")
  private String contact;

  @NotBlank(message = "Message cannot be blank.")
  private String message;

  private boolean resolved = false;

}
