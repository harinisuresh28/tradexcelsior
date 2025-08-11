package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.User;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepo extends MongoRepository<User, String> {
//  Optional<User> findByEmail(String email);
//  Optional<User> findByMobileNumber(String mobileNumber);
//
//  Page<User> findAll(Pageable pageable);
//  long countBySubscriptionEndDateAfter(LocalDate date);

  //*********** Methods to filter Deleted ****************//

  // Custom query to fetch user by email and ensure isDeleted is false
  Optional<User> findByEmailAndIsDeletedFalse(String email);

  // Custom query to fetch user by mobile number and ensure isDeleted is false
  Optional<User> findByMobileNumberAndIsDeletedFalse(String mobileNumber);

  // Custom query to fetch all users (paginated) where isDeleted is false
  Page<User> findByIsDeletedFalse(Pageable pageable);

  // Custom query to count users with subscriptionEndDate after a specific date and ensure isDeleted is false
  long countBySubscriptionEndDateAfterAndIsDeletedFalse(LocalDate date);

  // Custom query to fetch user by id and check if isDeleted is false
  Optional<User> findByIdAndIsDeletedFalse(String id);

  // Count only users where isDeleted is false
  long countByIsDeletedFalse();
}
