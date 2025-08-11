package com.tp.tradexcelsior.dto.response;

import lombok.Data;

@Data
public class LoginResponseDto {
  private String jwtToken;
  private String username;
}
