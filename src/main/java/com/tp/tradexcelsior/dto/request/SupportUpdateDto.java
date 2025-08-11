package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportUpdateDto {

  @NotBlank(message = "SupportId cannot be blank.")
  private String supportId;

  private boolean resolved;

}
