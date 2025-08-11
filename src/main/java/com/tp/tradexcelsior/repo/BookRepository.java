package com.tp.tradexcelsior.repo;

import com.tp.tradexcelsior.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {

    Optional<Book> findByNameAndDescriptionAndIsDeletedFalse(String name, String description);

    List<Book> findByIsDeletedFalse();

    Optional<Book> findByIdAndIsDeletedFalse(String id);

    List<Book> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);
}