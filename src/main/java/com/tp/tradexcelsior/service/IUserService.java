package com.tp.tradexcelsior.service;


import com.tp.tradexcelsior.dto.request.AddUserDto;
import com.tp.tradexcelsior.dto.request.ResetPasswordDto;
import com.tp.tradexcelsior.dto.request.SetPasswordDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.UserResponseDto;
import com.tp.tradexcelsior.dto.response.UsersCountWithStatus;
import com.tp.tradexcelsior.util.ResponseWrapper;

public interface IUserService {
  ResponseWrapper<UserResponseDto> addUser(AddUserDto addUserDto);
  ResponseWrapper<UserResponseDto> getUser(String id);
  ResponseWrapper<PagedResponse<UserResponseDto>> getUsersList(int page, int size);
  ResponseWrapper<UserResponseDto> updateUser(AddUserDto addUserDto, String id);
  ResponseWrapper<String> deleteUser(String id);
  ResponseWrapper<PagedResponse<UserResponseDto>> searchUsers(String name, String email, String mobileNumber, int page, int size);
  ResponseWrapper<UserResponseDto> updateSubscription(String userId, int extendSubscriptionByYear);
  ResponseWrapper<UsersCountWithStatus> getUsersStatusCount();
  ResponseWrapper<UserResponseDto> setPassword(SetPasswordDto setPasswordDto, String userId);
  ResponseWrapper<UserResponseDto> resetPassword(ResetPasswordDto resetPasswordDto, String userId);
}
