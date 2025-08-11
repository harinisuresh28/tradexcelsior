package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.request.BookRequestDto;
import com.tp.tradexcelsior.dto.response.BookResponseDto;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface IBookService {
    ResponseWrapper<List<BookResponseDto>> getAllBooks();
    ResponseWrapper<BookResponseDto> getBookById(String id);
    ResponseWrapper<BookResponseDto> createBook(BookRequestDto bookRequestDto, MultipartFile image);
    ResponseWrapper<BookResponseDto> updateBook(String id, BookRequestDto updatedBookRequestDto, MultipartFile image);
    ResponseWrapper<String> deleteBook(String id);
    ResponseWrapper<List<BookResponseDto>> searchBooks(String name);

}
