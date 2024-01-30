package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.errors.ConflictException;
import com.boardcamp.api.errors.NotFoundException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomersRepository;

import lombok.NonNull;

@Service
public class CustomersService {
    final CustomersRepository customersRepository;

    CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public CustomerModel save(CustomerDTO dto) {
        if (customersRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("Customer already exists with the given cpf!");
        }

        CustomerModel customer = new CustomerModel(dto);
        return customersRepository.save(customer);
    }

    public CustomerModel findById(@NonNull Long id) {
        return customersRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));
    }
}
