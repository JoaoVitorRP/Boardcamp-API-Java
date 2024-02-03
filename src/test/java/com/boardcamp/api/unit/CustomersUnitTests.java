package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.errors.ConflictException;
import com.boardcamp.api.errors.NotFoundException;
import com.boardcamp.api.factories.CustomersFactory;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomersRepository;
import com.boardcamp.api.services.CustomersService;

@SpringBootTest
class CustomersUnitTests {
    @InjectMocks
    private CustomersService customersService;

    @Mock
    private CustomersRepository customersRepository;

    @Test
    void givenRepeatedCpf_whenCreatingCustomer_thenThrowsError() {
        CustomerDTO customerDto = CustomersFactory.createValidCustomerDto();

        doReturn(true).when(customersRepository).existsByCpf(any());

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customersService.save(customerDto));

        verify(customersRepository, times(1)).existsByCpf(any());
        verify(customersRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Customer already exists with the given cpf!", exception.getMessage());
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenCreatesCustomer() {
        CustomerDTO customerDto = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDto);

        doReturn(false).when(customersRepository).existsByCpf(any());
        doReturn(customerModel).when(customersRepository).save(any());

        CustomerModel result = customersService.save(customerDto);

        verify(customersRepository, times(1)).existsByCpf(any());
        verify(customersRepository, times(1)).save(any());
        assertEquals(customerModel, result);
    }

    @Test
    void givenInvalidId_whenFetchingCustomerById_thenThrowsError() {
        Long id = 1L;

        doReturn(Optional.empty()).when(customersRepository).findById(any());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customersService.findById(id));

        verify(customersRepository, times(1)).findById(any());
        assertNotNull(exception);
        assertEquals("Customer not found!", exception.getMessage());
    }

    @Test
    void givenValidId_whenFetchingCustomerById_thenFetchesCustomer() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        customerModel.setId(1L);

        doReturn(Optional.of(customerModel)).when(customersRepository).findById(any());

        CustomerModel result = customersService.findById(customerModel.getId());
        
        verify(customersRepository, times(1)).findById(any());
        assertEquals(customerModel, result);
    }
}
