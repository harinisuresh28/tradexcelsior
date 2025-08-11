package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.Reference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends MongoRepository<Reference, String> {

    boolean existsByNameAndType(String name, String type);

    // Find all references that are not deleted
    List<Reference> findByIsDeletedFalse();

    // Find reference by id where isDeleted is false
    Optional<Reference> findByIdAndIsDeletedFalse(String id);

    // Find references by type that are not deleted
    List<Reference> findByTypeAndIsDeletedFalse(String type);

    // Find reference by name and type that are not deleted (for uniqueness check)
    Optional<Reference> findByNameAndTypeAndIsDeletedFalse(String name, String type);

    // Count references by type that are not deleted
    long countByTypeAndIsDeletedFalse(String type);

    // Find reference by name that is not deleted
    Optional<Reference> findByNameAndIsDeletedFalse(String name);
}
