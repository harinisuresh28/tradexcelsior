package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceRequestDto {

    private String id;

    @NotBlank(message = "Reference name cannot be empty.")
    private String name;

    @NotBlank(message = "Reference type cannot be empty.")
    private String type;

    @NotBlank(message = "Reference link cannot be empty.")
    private String link;
}
