package com.boardcamp.api.factories;

import com.boardcamp.api.dtos.CustomerDTO;

public class CustomersFactory {
    public static CustomerDTO createValidCustomerDto() {
        CustomerDTO customer = new CustomerDTO("Joao", "01234567890");

        return customer;
    }

    public static CustomerDTO createInvalidCustomerDto() {
        CustomerDTO customer = new CustomerDTO("Joao", "0");

        return customer;
    }
}
