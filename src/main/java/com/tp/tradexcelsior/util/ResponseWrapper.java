package com.tp.tradexcelsior.util;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
  private String status;
  private int code;
  private String message;
  private T data;
  private Map<String, String> errors;
  private LocalDateTime timestamp;

  public ResponseWrapper(String status, int code, String message, T data, Map<String, String> errors) {
    this.status = status;
    this.code = code;
    this.message = message;
    this.data = data;
    this.errors = errors;
    this.timestamp = LocalDateTime.now();
  }

  // Utility method to create success response
  public static <T> ResponseWrapper<T> success(int code, T data, String message) {
    return new ResponseWrapper<>("success", code, message, data, null);
  }

  // Utility method to create error response
  public static <T> ResponseWrapper<T> error(int code, Map<String, String> errors, String message) {
    return new ResponseWrapper<>("error", code, message, null, errors);
  }
}
