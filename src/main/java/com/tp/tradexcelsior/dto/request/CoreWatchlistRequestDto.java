package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreWatchlistRequestDto {

  @NotBlank(message = "Company name cannot be blank")
  @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
  private String company;

  @NotBlank(message = "Analysis link cannot be blank")
  @Pattern(regexp = "^https?://.*", message = "Analysis link must be a valid URL")
  private String analysisLink;

  @NotBlank(message = "Sector cannot be blank")
  @Size(min = 2, max = 50, message = "Sector must be between 2 and 50 characters")
  private String sector;

  @NotBlank(message = "Market capitalization cannot be blank")
  @Pattern(regexp = "^\\d+(\\.\\d{1,2})?([MB])?$", message = "Market cap must be a valid number with up to two decimal places, and optionally followed by 'M' or 'B'")
  private String marketCap;

}
