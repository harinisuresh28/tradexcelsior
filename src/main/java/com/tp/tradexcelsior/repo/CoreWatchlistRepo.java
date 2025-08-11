package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.CoreWatchlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CoreWatchlistRepo extends MongoRepository<CoreWatchlist, String> {

  // Find all CoreWatchlist entries that are not deleted
  List<CoreWatchlist> findByIsDeletedFalse();

  // Find all CoreWatchlist entries that are not deleted
  Page<CoreWatchlist> findByIsDeletedFalse(Pageable pageable);

  // Find a CoreWatchlist by ID where isDeleted is false
  Optional<CoreWatchlist> findByIdAndIsDeletedFalse(String id);

  // Count the number of CoreWatchlist entries that are not deleted
  long countByIsDeletedFalse();

  // Find CoreWatchlist by company name that is not deleted
  Optional<CoreWatchlist> findByCompanyAndIsDeletedFalse(String company);

}
