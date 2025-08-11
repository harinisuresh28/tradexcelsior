package com.tp.tradexcelsior.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceResponseDto {

    private String id;
    private String name;
    private String type;
    private String link;
    private String imageId;
}
