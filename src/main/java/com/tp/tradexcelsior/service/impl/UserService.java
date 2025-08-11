package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.criteria.UserSearchCriteriaBuilder;
import com.tp.tradexcelsior.dto.request.AddUserDto;
import com.tp.tradexcelsior.dto.request.ResetPasswordDto;
import com.tp.tradexcelsior.dto.request.SetPasswordDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.dto.response.UserResponseDto;
import com.tp.tradexcelsior.dto.response.UsersCountWithStatus;
import com.tp.tradexcelsior.entity.User;
import com.tp.tradexcelsior.exception.custom.UserAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.UserNotFoundException;
import com.tp.tradexcelsior.exception.custom.ValidationException;
import com.tp.tradexcelsior.repo.UserRepo;
import com.tp.tradexcelsior.service.IUserService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService implements IUserService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepo userRepo;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private EmailService emailService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public ResponseWrapper<UserResponseDto> addUser(AddUserDto addUserDto) {
    LocalDate subscriptionStartDate = LocalDate.now();
    LocalDate subscriptionEndDate = subscriptionStartDate
        .plusYears(addUserDto.getSubscriptionDuration())
        .minusDays(1);

    // Check if user with the same email exists
    userRepo.findByEmailAndIsDeletedFalse(addUserDto.getEmail())
        .ifPresent(existingUser -> {
          throw new UserAlreadyExistsException("Email is already in use.");
        });

    // Check if user with the same mobile number exists
    userRepo.findByMobileNumberAndIsDeletedFalse(addUserDto.getMobileNumber())
        .ifPresent(existingUser -> {
          throw new UserAlreadyExistsException("Mobile number is already in use.");
        });

    User user = modelMapper.map(addUserDto, User.class);
    user.setSubscriptionStartDate(subscriptionStartDate);
    user.setSubscriptionEndDate(subscriptionEndDate);
    user.setPassword(passwordEncoder.encode(addUserDto.getPassword()));

    try {
      User savedUser = userRepo.save(user);
      // Send email asynchronously and handle success/failure
      CompletableFuture<Boolean> emailResult = emailService.sendEmail(user.getEmail());

      // You can handle the result here or log if the email failed
      emailResult.thenAccept(success -> {
        if (!success) {
          // Handle email failure (e.g., log, retry, notify admin, etc.)
          log.error("Failed to send email to user: {}", user.getEmail());
        }

        if(success){
          log.info("Sent activation email");
        }
      });
      UserResponseDto responseDto = modelMapper.map(savedUser, UserResponseDto.class);
      return ResponseWrapper.success(HttpStatus.CREATED.value(), responseDto, "New user added.");
    } catch (DataIntegrityViolationException ex) {
      log.error("Error saving user: {}", ex.getMessage());
      throw new RuntimeException("User already exists or invalid data", ex);
    }
  }

  @Override
  public ResponseWrapper<UserResponseDto> getUser(String id) {
    User user = userRepo.findByIdAndIsDeletedFalse(id).orElseThrow(() -> {
      log.error("User not found with id: {}", id);
      return new UserNotFoundException("No user present with the id : " + id);
    });
    UserResponseDto responseDto = modelMapper.map(user, UserResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "User fetched with id: "+ id);
  }


  @Override
  public ResponseWrapper<PagedResponse<UserResponseDto>> getUsersList(int page, int size) {

    // If the page number is negative, throw an exception
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }

    // If the page number is negative, throw an exception
    if (size < 1) {
      throw new IllegalArgumentException("Page size can't be less than 1");
    }


    // Calculate the total number of users
    long totalItems = userRepo.countByIsDeletedFalse();  // Get total count of users
    System.out.println(totalItems);

    // Calculate total pages
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // If the requested page exceeds the available total pages, return the last page
    if (page >= totalPages && totalPages > 0) {
      PagedResponse<UserResponseDto> pagedResponse= new PagedResponse<>(Collections.emptyList(), (int) totalItems, totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of users fetched successfully.");
    }

    // Create a Pageable object for pagination with the corrected page number
    Pageable pageable = PageRequest.of(page, size);

    // Fetch the paginated results from the repository
    Page<User> usersPage = userRepo.findByIsDeletedFalse(pageable);

    // Convert the list of users to UserResponseDto
    List<UserResponseDto> userResponseDto = usersPage.getContent().stream()
        .map(user -> modelMapper.map(user, UserResponseDto.class))
        .collect(Collectors.toList());

    // Return a PagedResponse object containing the results, pagination information, and the total count
    PagedResponse<UserResponseDto> pagedResponse= new PagedResponse<>(userResponseDto, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of users fetched successfully.");
  }


  @Transactional
  public ResponseWrapper<UserResponseDto> updateUser(AddUserDto addUserDto, String id) {
    // Fetch the existing user from MongoDB
    User existingUser = userRepo.findByIdAndIsDeletedFalse(id).orElseThrow(() -> {
      log.error("User not found with id: {}", id);
      return new UserNotFoundException("No user with this user id: " + id);
    });

    // Check if another user exists with the same email (but not the current user)
    Optional<User> userWithEmail = userRepo.findByEmailAndIsDeletedFalse(addUserDto.getEmail());
    if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
      throw new UserAlreadyExistsException("A user with this email already exists");
    }

    // Check if another user exists with the same phone number (but not the current user)
    Optional<User> userWithPhoneNumber = userRepo.findByMobileNumberAndIsDeletedFalse(addUserDto.getMobileNumber());
    if (userWithPhoneNumber.isPresent() && !userWithPhoneNumber.get().getId().equals(id)) {
      throw new UserAlreadyExistsException("A user with this phone number already exists");
    }

    // Create an update object
    Update update = new Update();

    // Add the fields to be updated only if they're not null or empty
    if (addUserDto.getFirstName() != null && !addUserDto.getFirstName().isEmpty()) {
      update.set("firstName", addUserDto.getFirstName());
    }
    if (addUserDto.getLastName() != null && !addUserDto.getLastName().isEmpty()) {
      update.set("lastName", addUserDto.getLastName());
    }
    if (addUserDto.getMobileNumber() != null && !addUserDto.getMobileNumber().isEmpty()) {
      update.set("mobileNumber", addUserDto.getMobileNumber());
    }
    if (addUserDto.getAddress() != null && !addUserDto.getAddress().isEmpty()) {
      update.set("address", addUserDto.getAddress());
    }
    if (addUserDto.getOccupation() != null && !addUserDto.getOccupation().isEmpty()) {
      update.set("occupation", addUserDto.getOccupation());
    }
    if (addUserDto.getPassword() != null && !addUserDto.getPassword().isEmpty()) {
      update.set("password", passwordEncoder.encode(addUserDto.getPassword()));
    }

    update.set("lastModified", LocalDateTime.now());

    // Perform the update
    mongoTemplate.updateFirst(
        Query.query(Criteria.where("_id").is(id)),  // Find by the user ID
        update,  // Update object
        User.class  // Entity class
    );

    // Fetch and return the updated user
    User updatedUser = userRepo.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    UserResponseDto responseDto = modelMapper.map(updatedUser, UserResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "User updated.");
  }


  @Override
  @Transactional
  public ResponseWrapper<String> deleteUser(String id) {
    User user = userRepo.findByIdAndIsDeletedFalse(id).orElseThrow(() -> {
      log.error("User not found with id: {}", id);
      return new UserNotFoundException("User does not exist with id : " + id);
    });

    user.setDeleted(true);
    user.setLastModified(LocalDateTime.now());
    userRepo.save(user);
    return ResponseWrapper.success(HttpStatus.OK.value(), "User id: "+ id, "Deleted successfully.");
  }


  @Override
  public ResponseWrapper<PagedResponse<UserResponseDto>> searchUsers(String name, String email, String mobileNumber, int page, int size) {

    // If the page number is negative, throw an exception
    if (page < 0) {
      throw new IllegalArgumentException("Page number cannot be negative");
    }

    // If the page number is negative, throw an exception
    if (size < 1) {
      throw new IllegalArgumentException("Page size can't be less than 1");
    }

    // Build the query using the helper class
    Query query = new Query();
    query.addCriteria(UserSearchCriteriaBuilder.buildSearchCriteria(name, email, mobileNumber));

    // Calculate the total number of matching records based on search criteria
    long totalItems = mongoTemplate.count(query, User.class); // This counts the documents based on the search query

    // Calculate total pages
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // If totalPages is 0, set the page to 0
    if (totalPages == 0) {
      page = 0;
    }

    // If the requested page exceeds the available total pages, return the last page
    if (page >= totalPages && totalPages > 0) {
      PagedResponse<UserResponseDto> pagedResponse= new PagedResponse<>(Collections.emptyList(), (int) totalItems, totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of searched users.");
    }

    // Create a Pageable object for pagination with the corrected page number
    Pageable pageable = PageRequest.of(page, size);

    // Apply pagination and fetch the results based on the query and pageable
    query.with(pageable);

    // Fetch the paginated results from the repository
    List<User> users = mongoTemplate.find(query, User.class);

    // Convert the list of users to UserResponseDto
    List<UserResponseDto> userResponseDto = users.stream()
        .map(user -> modelMapper.map(user, UserResponseDto.class))
        .collect(Collectors.toList());

    // Return a PagedResponse object containing the results, pagination information, and the total count

    PagedResponse<UserResponseDto> pagedResponse= new PagedResponse<>(userResponseDto, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of searched users.");
  }


  @Override
  @Transactional
  public ResponseWrapper<UserResponseDto> updateSubscription(String userId, int extendSubscriptionByYear) {
    // Edge case: Ensure the subscription duration is valid (greater than zero)
    if (extendSubscriptionByYear <= 0) {
      throw new ValidationException("Subscription duration must be a positive number.");
    }

    // Step 1: Fetch the user from the database by userId
    User user = userRepo.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> {
      throw new UserNotFoundException("User with ID " + userId + " not found");
    });

    // Step 2: Update only the subscription fields
    LocalDate currentDate = LocalDate.now();
    user.setSubscriptionDuration(user.getSubscriptionDuration() + extendSubscriptionByYear); // Add years to existing duration
    user.setSubscriptionStartDate(currentDate);
    user.setSubscriptionEndDate(currentDate.plusYears(user.getSubscriptionDuration()));
    user.setLastModified(LocalDateTime.now());

    // Step 3: Save the updated user back to the database
    User updatedUser = userRepo.save(user);

    // Return the updated user details in the response format
    UserResponseDto responseDto = modelMapper.map(updatedUser, UserResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Subscription updated.");
  }

  @Override
  public ResponseWrapper<UsersCountWithStatus> getUsersStatusCount() {
    LocalDate currentDate = LocalDate.now();

    long totalUsers = userRepo.countByIsDeletedFalse();
    long activeUsers = userRepo.countBySubscriptionEndDateAfterAndIsDeletedFalse(currentDate);
    long inactiveUsers = totalUsers - activeUsers;

    UsersCountWithStatus usersCountWithStatus= new UsersCountWithStatus(totalUsers,  activeUsers, inactiveUsers);
    return ResponseWrapper.success(HttpStatus.OK.value(), usersCountWithStatus, "Users count with status.");
  }


  @Override
  public ResponseWrapper<UserResponseDto> setPassword(SetPasswordDto setPasswordDto, String userId) {
    // Password mismatch validation
    if (!setPasswordDto.getPassword().equals(setPasswordDto.getConfirmPassword())) {
      throw new IllegalArgumentException("Password and confirm password do not match!");
    }

    // Fetch the user by userId
    User user = userRepo.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new UserNotFoundException("User does not exist with id: " + userId));

    // Set the new password for the user
    user.setPassword(passwordEncoder.encode(setPasswordDto.getPassword()));
    user.setLastModified(LocalDateTime.now());
    User updatedUser = userRepo.save(user); // Save the updated user to DB

    UserResponseDto userResponseDto = modelMapper.map(updatedUser, UserResponseDto.class);

    return ResponseWrapper.success(HttpStatus.OK.value(), userResponseDto, "New password set successfully.");
  }

  @Override
  public ResponseWrapper<UserResponseDto> resetPassword(ResetPasswordDto resetPasswordDto, String userId) {

    User user = userRepo.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new UserNotFoundException("User does not exist with id: " + userId));

    // Compare the old password with the stored password
    if (!user.getPassword().equals(resetPasswordDto.getOldPassword())) {
      throw new IllegalArgumentException("Old password does not match!");
    }

    // Password mismatch validation (new password and confirm password)
    if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
      throw new IllegalArgumentException("New password and confirm password do not match!");
    }

    // Set the new password for the user
    user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
    user.setLastModified(LocalDateTime.now());
    User updatedUser = userRepo.save(user); // Save the updated user to DB

    UserResponseDto userResponseDto = modelMapper.map(updatedUser, UserResponseDto.class);

    return ResponseWrapper.success(HttpStatus.OK.value(), userResponseDto, "Password changed successfully.");
  }

}
