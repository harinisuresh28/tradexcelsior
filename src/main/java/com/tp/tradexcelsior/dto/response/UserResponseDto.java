package com.tp.tradexcelsior.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

  private String id;
  private String firstName;
  private String lastName;
  private String occupation;
  private String mobileNumber;
  private String email;
  private String licence;
  private String address;
  private LocalDate subscriptionStartDate;
  private LocalDate subscriptionEndDate;
  private int subscriptionDuration;

}
