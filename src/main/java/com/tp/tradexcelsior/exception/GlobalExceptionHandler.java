package com.tp.tradexcelsior.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tp.tradexcelsior.exception.custom.BookAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.BookNotFoundException;
import com.tp.tradexcelsior.exception.custom.ChecklistAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ChecklistNotFoundException;
import com.tp.tradexcelsior.exception.custom.CoreWatchlistAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.CoreWatchlistNotFoundException;
import com.tp.tradexcelsior.exception.custom.ImageAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ImageNotFoundException;
import com.tp.tradexcelsior.exception.custom.ReferenceAlreadyExistException;
import com.tp.tradexcelsior.exception.custom.ReferenceNotFoundException;
import com.tp.tradexcelsior.exception.custom.SuccessStoryAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.SuccessStoryNotFoundException;
import com.tp.tradexcelsior.exception.custom.SupportNotFoundException;
import com.tp.tradexcelsior.exception.custom.UserAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.UserNotFoundException;
import com.tp.tradexcelsior.exception.custom.ValidationException;
import com.tp.tradexcelsior.util.ResponseWrapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Not Found Exception
    @ExceptionHandler({
        ChecklistNotFoundException.class,
        ImageNotFoundException.class,
        BookNotFoundException.class,
        UserNotFoundException.class,
        ReferenceNotFoundException.class,
        SupportNotFoundException.class,
        CoreWatchlistNotFoundException.class,
        SuccessStoryNotFoundException.class
    })
    public ResponseEntity<ResponseWrapper<Object>> handleNotFoundException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.NOT_FOUND.value(), errors, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle Duplicate Exception
    @ExceptionHandler({
        ChecklistAlreadyExistException.class,
        BookAlreadyExistException.class,
        UserAlreadyExistsException.class,
        CoreWatchlistAlreadyExistsException.class,
        ReferenceAlreadyExistException.class,
        SuccessStoryAlreadyExistsException.class,
        ImageAlreadyExistException.class
    })
    public ResponseEntity<ResponseWrapper<Object>> handleAlreadyExistException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.CONFLICT.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle ValidationException
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleValidationException(ValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle malformed JSON (HttpMessageNotReadableException)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleMalformedJson(HttpMessageNotReadableException ex) {
        String errorMessage = "Malformed JSON request. Please check your syntax and ensure proper formatting.";
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle JSON parse exception (e.g., invalid JSON format, trailing commas)
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleJsonParseException(JsonParseException ex) {
        String errorMessage = "Invalid JSON format: " + ex.getMessage();
        Map<String, String> errors = new HashMap<>();
        errors.put("message", errorMessage);
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle JSON mapping exception (e.g., invalid or missing fields for expected DTO)
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleJsonMappingException(JsonMappingException ex) {
        String errorMessage = "Error mapping JSON: " + ex.getMessage();
        Map<String, String> errors = new HashMap<>();
        errors.put("message", errorMessage);
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle IllegalArgumentException (bad requests)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle RuntimeException (generic exception handler)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors, "Something went wrong!");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle Generic Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleGenericException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors, "Something went wrong!");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Invalid data!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle AuthorizationDeniedException (access denied error)
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.FORBIDDEN.value(), errors, "Access Denied");

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle AuthorizationDeniedException (access denied error)
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Access Denied");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        ResponseWrapper<Object> errorResponse = ResponseWrapper.error(HttpStatus.BAD_REQUEST.value(), errors, "Access Denied");

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



}
