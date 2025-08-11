package com.tp.tradexcelsior.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDto {
  private String imageId;
  private String filename;
  private String contentType;
}