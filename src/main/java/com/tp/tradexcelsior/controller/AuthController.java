package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.request.LoginDto;
import com.tp.tradexcelsior.dto.response.LoginResponseDto;
import com.tp.tradexcelsior.service.impl.AuthService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Management", description = "APIs for handling authentication")
@RestController
@RequestMapping("auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @Autowired
  PasswordEncoder passwordEncoder;

  // Build Login REST API
  @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
  @PostMapping("/login")
  public ResponseEntity<ResponseWrapper<LoginResponseDto>> login(@RequestBody @Valid LoginDto loginDto){
    ResponseWrapper<LoginResponseDto> loginResponseDto = authService.login(loginDto);

    return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
  }

}
