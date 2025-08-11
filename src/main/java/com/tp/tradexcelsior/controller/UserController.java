package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.request.AddUserDto;
import com.tp.tradexcelsior.dto.request.ResetPasswordDto;
import com.tp.tradexcelsior.dto.request.SetPasswordDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.UserResponseDto;
import com.tp.tradexcelsior.dto.response.UsersCountWithStatus;
import com.tp.tradexcelsior.exception.custom.ValidationException;
import com.tp.tradexcelsior.service.impl.UserService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired
  private UserService userService;

  // Create a new user
  @Operation(summary = "Create a new user", description = "Create a new user by providing necessary details")
  @PostMapping
  public ResponseEntity<ResponseWrapper<UserResponseDto>> addUser(@RequestBody @Valid AddUserDto addUserDto,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      // Collecting validation errors
      String errorMessages = bindingResult.getAllErrors().stream()
          .map(error -> error.getDefaultMessage())
          .reduce((msg1, msg2) -> msg1 + ", " + msg2)
          .orElse("Validation failed");

      // Throw a custom exception with error details
      throw new ValidationException(errorMessages);
    }

    ResponseWrapper<UserResponseDto> userResponseDto = userService.addUser(addUserDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
  }

  // Get a user by ID
  @Operation(summary = "Get a user by ID", description = "Fetch the details of a user by their ID")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and authentication.principal.id == #id)")
  @GetMapping("/{id}")
  public ResponseEntity<ResponseWrapper<UserResponseDto>> getUser(@PathVariable String id) {
    ResponseWrapper<UserResponseDto> userResponseDto = userService.getUser(id);
    return ResponseEntity.ok(userResponseDto);
  }

  // Get a paginated list of users
  @Operation(summary = "Get a paginated list of users", description = "Retrieve a paginated list of users")
  @GetMapping
  public ResponseEntity<ResponseWrapper<PagedResponse<UserResponseDto>>> getUsersList(
      @RequestParam(defaultValue = "0") int page,  // Default to the first page
      @RequestParam(defaultValue = "10") int size   // Default to a page size of 10
  ) {
    ResponseWrapper<PagedResponse<UserResponseDto>> pagedResponse = userService.getUsersList(page, size);
    return ResponseEntity.ok(pagedResponse);
  }

  // Update a user's information
  @Operation(summary = "Update a user's information", description = "Update the details of an existing user")
  @PutMapping("/{id}")
  public ResponseEntity<ResponseWrapper<UserResponseDto>> updateUser(@RequestBody @Valid AddUserDto addUserDto,
      BindingResult bindingResult, @PathVariable String id) {
    if (bindingResult.hasErrors()) {
      // Collecting validation errors
      String errorMessages = bindingResult.getAllErrors().stream()
          .map(error -> error.getDefaultMessage())
          .reduce((msg1, msg2) -> msg1 + ", " + msg2)
          .orElse("Validation failed");

      // Throw a custom exception with error details
      throw new ValidationException(errorMessages);
    }

    ResponseWrapper<UserResponseDto> updatedUser = userService.updateUser(addUserDto, id);
    return ResponseEntity.ok(updatedUser);
  }

  // Delete a user by ID
  @Operation(summary = "Delete a user by ID", description = "Delete a user from the system by their ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseWrapper<String>> deleteUser(@PathVariable String id) {
    ResponseWrapper<String> response= userService.deleteUser(id);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Update user subscription", description = "Extend or modify a user's subscription")
  @PutMapping("/updateSubscription/{userId}")
  public ResponseEntity<ResponseWrapper<UserResponseDto>> updateSubscription(@PathVariable String userId, @RequestParam int extendSubscriptionByYear) {
    ResponseWrapper<UserResponseDto> updatedUser = userService.updateSubscription(userId,
        extendSubscriptionByYear);
    return ResponseEntity.ok(updatedUser);
  }

  // Search users by parameters
  @Operation(summary = "Search users by parameters", description = "Search users based on optional parameters like name, email, or mobile number")
  @GetMapping("/search")
  public ResponseEntity<ResponseWrapper<PagedResponse<UserResponseDto>>> searchUsers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String mobileNumber,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {


    // Fetch paginated results from the service
    ResponseWrapper<PagedResponse<UserResponseDto>> searchResults = userService.searchUsers(name, email,
        mobileNumber, page, size);

    // Return a ResponseEntity with status 200 and the paginated results
    return ResponseEntity.ok(searchResults);
  }

  @Operation(
      summary = "Get users subscription status count",
      description = "Retrieve the count of users based on their subscription status (active or inactive)"
  )
  @GetMapping("/status")
  public ResponseEntity<ResponseWrapper<UsersCountWithStatus>> getUsersStatusCount() {
    ResponseWrapper<UsersCountWithStatus> usersCountWithStatus = userService.getUsersStatusCount();
    return ResponseEntity.ok(usersCountWithStatus);
  }

  @Operation(summary = "Set the password for a user", description = "Allows a user to set a new password for their account.")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and authentication.principal.id == #userId)")
  @PutMapping("/set-password/{userId}")
  public ResponseEntity<ResponseWrapper<UserResponseDto>> setPassword(
      @RequestBody @Valid SetPasswordDto setPasswordDto, @PathVariable String userId) {
    ResponseWrapper<UserResponseDto> response = userService.setPassword(setPasswordDto, userId);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Reset the password for a user", description = "Allows a user to reset their password by verifying the old password and setting a new one.")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and authentication.principal.id == #userId)")
  @PutMapping("/reset-password/{userId}")
  public ResponseEntity<ResponseWrapper<UserResponseDto>> resetPassword(
      @RequestBody @Valid ResetPasswordDto resetPasswordDto, @PathVariable String userId) {

    ResponseWrapper<UserResponseDto> response = userService.resetPassword(resetPasswordDto, userId);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}
