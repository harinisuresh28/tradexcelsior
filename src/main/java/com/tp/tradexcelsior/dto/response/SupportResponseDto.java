package com.tp.tradexcelsior.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportResponseDto {
  private String id;
  private String name;
  private String email;
  private String contact;
  private String message;
  private boolean resolved;
}
