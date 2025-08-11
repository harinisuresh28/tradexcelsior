package com.tp.tradexcelsior.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequestDto {

    private String id;

    @NotBlank(message = "Book name cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Tagline cannot be empty")
    private String tagLine;

    @NotBlank(message = "Link to buy book cannot be empty")
    private String linkToBuyBook;

    @NotBlank(message = "Button name cannot be empty")
    private String buttonName;

}