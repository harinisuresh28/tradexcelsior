package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.SuccessStories;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuccessStoriesRepo extends MongoRepository<SuccessStories, String> {

  // Find non-deleted success stories with pagination
  Page<SuccessStories> findByIsDeletedFalse(Pageable pageable);

  // Count non-deleted success stories
  long countByIsDeletedFalse();

  // Find a non-deleted success story by userName
  Optional<SuccessStories> findByUserNameAndIsDeletedFalse(String userName);
}
