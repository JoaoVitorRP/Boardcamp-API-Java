package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GameDTO {
    @NotBlank(message = "Field \"name\" cannot be blank!")
    public String name;

    @NotBlank(message = "Field \"image\" cannot be blank!")
    public String image;

    @NotNull(message = "Field \"stock total\" cannot be null!")
    @Positive(message = "Field \"stock total\" must be greater than 0")
    public Long stockTotal;

    @NotNull(message = "Field \"price per day\" cannot be null")
    @Positive(message = "Field \"price per day\" must be greater than 0")
    public Long pricePerDay;
}
