package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {
    @NotNull(message = "Field \"customer id\" cannot be null!")
    private Long customerId;

    @NotNull(message = "Field \"game id\" cannot be null!")
    private Long gameId;

    @NotNull(message = "Field \"days rented\" cannot be null!")
    @Positive(message = "Field \"days rented\" must be greater than 0")
    private Long daysRented;
}
