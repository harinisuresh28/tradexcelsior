package com.tp.tradexcelsior.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.tradexcelsior.dto.request.BookRequestDto;
import com.tp.tradexcelsior.dto.request.SuccessStoriesRequestDto;
import com.tp.tradexcelsior.dto.response.BookResponseDto;
import com.tp.tradexcelsior.dto.response.SuccessStoriesResponseDto;
import com.tp.tradexcelsior.service.impl.BookService;
import com.tp.tradexcelsior.service.impl.ImageService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Book Management", description = "APIs for managing books")
@RestController
@RequestMapping("/api/v1/book")
public class BookController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private Validator validator;

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get all books", description = "Fetch all available books")
    @GetMapping
    public ResponseEntity<ResponseWrapper<List<BookResponseDto>>> getAllBooks() {
        ResponseWrapper<List<BookResponseDto>> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @Operation(summary = "Add a new book", description = "Create a new book in the system")
    @PostMapping
    public ResponseEntity<ResponseWrapper<BookResponseDto>> createBook(@RequestPart("book") String bookDTOString, @RequestPart(value = "image", required = false) MultipartFile image) {
        BookRequestDto bookRequestDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            bookRequestDto = objectMapper.readValue(bookDTOString, BookRequestDto.class);
        } catch (IOException e) {
            // Handle JSON parsing error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
        }

        // Validate the DTO manually
        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(bookRequestDto);
        if (!violations.isEmpty()) {
            // Collect validation errors
            Map<String, String> errorMessages = new HashMap<>();
            for (ConstraintViolation<BookRequestDto> violation : violations) {
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
        }

        // If valid, proceed with the service logic
        ResponseWrapper<BookResponseDto> bookResponseDto = bookService.createBook(bookRequestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseDto);
    }


    @Operation(summary = "Get book by ID", description = "Fetch a book using its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BookResponseDto>> getBookById(@PathVariable String id) {
        ResponseWrapper<BookResponseDto> bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(bookDTO);
    }

    @Operation(summary = "Update book details", description = "Modify an existing book using its ID")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseWrapper<BookResponseDto>> updateBook(@PathVariable String id, @RequestPart("book") String bookDTOString, @RequestPart(value = "image", required = false) MultipartFile image) {
        BookRequestDto bookRequestDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            bookRequestDto = objectMapper.readValue(bookDTOString, BookRequestDto.class);
        } catch (IOException e) {
            // Handle JSON parsing error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), Map.of("message", "Invalid JSON format"), "Invalid data!"));
        }

        // Validate the DTO manually
        Set<ConstraintViolation<BookRequestDto>> violations = validator.validate(bookRequestDto);
        if (!violations.isEmpty()) {
            // Collect validation errors
            Map<String, String> errorMessages = new HashMap<>();
            for (ConstraintViolation<BookRequestDto> violation : violations) {
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errorMessages, "Invalid data!"));
        }

        ResponseWrapper<BookResponseDto> response = bookService.updateBook(id, bookRequestDto, image);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Delete a book", description = "Soft delete a book by marking it as inactive")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseWrapper<String>> deleteBook(@PathVariable String id) {
        ResponseWrapper<String> response = bookService.deleteBook(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search book by name.", description = "Get the list of search result of book by its name.")
    public ResponseEntity<ResponseWrapper<List<BookResponseDto>>> searchBooks(@RequestParam String name) {
        ResponseWrapper<List<BookResponseDto>> searchedBooks = bookService.searchBooks(name);
        return ResponseEntity.ok(searchedBooks);
    }
}