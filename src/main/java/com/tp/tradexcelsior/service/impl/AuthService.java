package com.tp.tradexcelsior.service.impl;


import com.tp.tradexcelsior.dto.request.LoginDto;
import com.tp.tradexcelsior.dto.response.LoginResponseDto;
import com.tp.tradexcelsior.security.JwtTokenProvider;
import com.tp.tradexcelsior.util.ResponseWrapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  public ResponseWrapper<LoginResponseDto> login(LoginDto loginDto) {
    LoginResponseDto loginResponseDto = new LoginResponseDto();
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        loginDto.getUsername(),
        loginDto.getPassword()
    ));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = jwtTokenProvider.generateToken(authentication);
    loginResponseDto.setJwtToken(token);
    loginResponseDto.setUsername(loginDto.getUsername());

    return ResponseWrapper.success(HttpStatus.OK.value(), loginResponseDto, "Logged in successfully.");
  }
}
