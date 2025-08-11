package com.tp.tradexcelsior.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStoriesImages extends CommonEntity{
  @Id
  private String id;
  private String filename;
  private String contentType;
  private byte[] content;
}
