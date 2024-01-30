package com.boardcamp.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {
    @NotBlank(message = "Field \"name\" cannot be blank!")
    public String name;

    @NotBlank(message = "Field \"cpf\" cannot be blank!")
    @Size(min = 11, max = 11, message = "Field \"cpf\" must have a length of 11 characters!")
    public String cpf;
}
