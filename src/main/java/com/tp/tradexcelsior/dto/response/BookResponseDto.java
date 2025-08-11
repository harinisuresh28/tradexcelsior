package com.tp.tradexcelsior.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {

    private String id;

    private String name;
    private String description;
    private String tagLine;
    private String linkToBuyBook;
    private String buttonName;
    private String imageId;

}