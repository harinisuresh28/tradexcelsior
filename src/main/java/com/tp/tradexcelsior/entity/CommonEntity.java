package com.tp.tradexcelsior.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonEntity {
  private boolean isDeleted = false;
  private LocalDateTime created = LocalDateTime.now();
  private LocalDateTime lastModified = LocalDateTime.now();
}
