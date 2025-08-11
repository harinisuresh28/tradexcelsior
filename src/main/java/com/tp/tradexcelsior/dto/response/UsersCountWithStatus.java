package com.tp.tradexcelsior.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersCountWithStatus {
  private long totalUsers;
  private long activeUsers;
  private long inactiveUsers;
}
