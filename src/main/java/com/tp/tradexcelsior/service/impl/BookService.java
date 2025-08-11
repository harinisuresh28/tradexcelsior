package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.dto.request.BookRequestDto;
import com.tp.tradexcelsior.dto.request.SuccessStoriesRequestDto;
import com.tp.tradexcelsior.dto.response.BookResponseDto;
import com.tp.tradexcelsior.dto.response.SuccessStoriesResponseDto;
import com.tp.tradexcelsior.entity.Book;
import com.tp.tradexcelsior.entity.SuccessStories;
import com.tp.tradexcelsior.exception.custom.BookAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.BookNotFoundException;
import com.tp.tradexcelsior.exception.custom.SuccessStoryNotFoundException;
import com.tp.tradexcelsior.repo.BookRepository;
import com.tp.tradexcelsior.service.IBookService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class BookService implements IBookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    @Override
    public ResponseWrapper<List<BookResponseDto>> getAllBooks() {
        log.info("Fetching all books from the database");
        List<Book> books = bookRepository.findByIsDeletedFalse();
        log.info("Total books found: {}", books.size());

        List<BookResponseDto> bookResponseDtoList = books.stream().map(book -> modelMapper.map(book, BookResponseDto.class)).toList();
        return ResponseWrapper.success(HttpStatus.OK.value(), bookResponseDtoList, "List of all books");
    }

    @Override
    public ResponseWrapper<BookResponseDto> getBookById(String id) {
        log.info("Fetching book with ID: {}", id);
        return bookRepository.findByIdAndIsDeletedFalse(id)
                .map(book -> {
                    log.info("Book found: {}", book.getName());
                    BookResponseDto bookResponseDto = modelMapper.map(book, BookResponseDto.class);
                    return ResponseWrapper.success(HttpStatus.OK.value(), bookResponseDto, "Book fetched.");
                })
                .orElseThrow(() -> {
                    log.error("Book with ID {} not found", id);
                    return new BookNotFoundException("Book with ID " + id + " not found.");
                });
    }


    @Override
    public ResponseWrapper<BookResponseDto> createBook(BookRequestDto bookRequestDto, MultipartFile image) {
        log.info("Creating new book: {}", bookRequestDto.getName());

        bookRepository.findByNameAndDescriptionAndIsDeletedFalse(bookRequestDto.getName(), bookRequestDto.getDescription())
                .ifPresent(existingBook -> {
                    log.warn("Duplicate book found: {}", bookRequestDto.getName());
                    throw new BookAlreadyExistException("A book with the same name and description already exists.");
                });

        Book book = modelMapper.map(bookRequestDto, Book.class);

        if(image!=null) {
            Map<String, String> imageByName = null;

            try {
                imageByName = imageService.uploadImageWithCustomName(image, book.getName() + "_book");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            book.setImageId(imageByName.get("imageId"));
        }

        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with ID: {}", savedBook.getId());
        BookResponseDto savedBookRequestDto = modelMapper.map(savedBook, BookResponseDto.class);
        return ResponseWrapper.success(HttpStatus.CREATED.value(), savedBookRequestDto, "New book saved.");
    }

    @Override
    public ResponseWrapper<BookResponseDto> updateBook(String id, BookRequestDto updatedBookRequestDto, MultipartFile image) {
        log.info("Updating book with ID: {}", id);

        Book existingBook = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> {
                    log.error("Cannot update. Book with ID {} not found", id);
                    return new BookNotFoundException("Cannot update. Book with ID " + id + " not found.");
                });

        // Update book details
        updatedBookRequestDto.setId(existingBook.getId());

        if(image!=null) {
            Map<String, String> imageByName = null;

            try {
                imageByName = imageService.uploadImageWithCustomName(image, updatedBookRequestDto.getName() + "_book");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            existingBook.setImageId(imageByName.get("imageId"));
        }

        existingBook = modelMapper.map(updatedBookRequestDto, Book.class);
        existingBook.setLastModified(LocalDateTime.now());
        // Save updated book
        Book savedBook = bookRepository.save(existingBook);
        log.info("Book updated successfully with ID: {}", savedBook.getId());
        BookResponseDto savedBookResponseDto = modelMapper.map(savedBook, BookResponseDto.class);
        return ResponseWrapper.success(HttpStatus.OK.value(), savedBookResponseDto, "Book updated.");
    }

    @Override
    public ResponseWrapper<String> deleteBook(String id) {
        Book existingBook = bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new BookNotFoundException("Cannot delete. Book with ID " + id + " not found."));

        // Mark the book as deleted
        existingBook.setDeleted(true);
        existingBook.setLastModified(LocalDateTime.now());
        bookRepository.save(existingBook); // Save the updated book (soft delete)

        return ResponseWrapper.success(HttpStatus.OK.value(), "Book id: "+ id, "Deleted successfully.");
    }

    @Override
    public ResponseWrapper<List<BookResponseDto>> searchBooks(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Book name cannot be empty");
        }

        List<Book> books = bookRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(name);
        List<BookResponseDto> bookResponseDtoList = books.stream().map(book -> modelMapper.map(book, BookResponseDto.class)).toList();
        return ResponseWrapper.success(HttpStatus.OK.value(), bookResponseDtoList, "List of searched books.");
    }
}