package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.Checklist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends MongoRepository<Checklist, String>  {
    boolean existsByDescriptionAndButtonNameAndIsDeletedFalse(String description, String buttonName);

    List<Checklist> findByIsDeletedFalse();

    Optional<Checklist> findByIdAndIsDeletedFalse(String id);
}
