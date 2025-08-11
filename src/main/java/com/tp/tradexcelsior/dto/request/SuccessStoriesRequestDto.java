package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStoriesRequestDto {

  private String id;

  @NotNull(message = "User name cannot be null")
  @NotEmpty(message = "User name cannot be empty")
  @Size(min = 1, max = 100, message = "User name must be between 1 and 100 characters")
  private String userName;

  @NotNull(message = "Feedback cannot be null")
  @NotEmpty(message = "Feedback cannot be empty")
  @Size(min = 10, max = 1000, message = "Feedback must be between 10 and 1000 characters")
  private String feedback;

  @NotNull(message = "Video URL cannot be null")
  @NotEmpty(message = "Video URL cannot be empty")
  private String videoUrl;

  @NotNull(message = "Tagline cannot be null")
  @NotEmpty(message = "Tagline cannot be empty")
  @Size(max = 200, message = "Tagline cannot be longer than 200 characters")
  private String tagline;

}
