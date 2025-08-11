package com.tp.tradexcelsior.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Support extends CommonEntity{

  @Id
  private String id;

  private String name;
  private String email;
  private String contact;
  private String message;
  private boolean resolved;

}
