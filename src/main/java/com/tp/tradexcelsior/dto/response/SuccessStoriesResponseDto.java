package com.tp.tradexcelsior.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessStoriesResponseDto {

  private String id;
  private String userName;
  private String feedback;
  private String videoUrl;
  private String tagline;
  private String imageId;
}
