package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistTrendUpdateDto {

  @NotBlank
  private String company;

  private String trend;

}
