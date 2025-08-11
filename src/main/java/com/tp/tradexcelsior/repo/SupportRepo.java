package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.Support;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SupportRepo extends MongoRepository<Support, String> {

  // Paginate support entries where isDeleted = false (non-deleted)
  Page<Support> findByIsDeletedFalse(Pageable pageable);

  Page<Support> findByResolvedTrueAndIsDeletedFalse(Pageable pageable);

  Page<Support> findByResolvedFalseAndIsDeletedFalse(Pageable pageable);

  // Count the number of non-deleted support entries
  long countByIsDeletedFalse();

  // Count the number of resolved (non-deleted) support entries
  long countByResolvedTrueAndIsDeletedFalse();

  // Count the number of unresolved (non-deleted) support entries
  long countByResolvedFalseAndIsDeletedFalse();

  // Find support entries that are non-deleted by their support ID
  Optional<Support> findByIdAndIsDeletedFalse(String id);

}
