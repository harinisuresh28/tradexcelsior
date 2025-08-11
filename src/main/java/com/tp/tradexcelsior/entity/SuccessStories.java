package com.tp.tradexcelsior.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStories extends CommonEntity{

  @Id
  private String id;

  @Indexed(unique = true)
  private String userName;

  private String feedback;
  private String videoUrl;
  private String tagline;
  private String imageId;

}
