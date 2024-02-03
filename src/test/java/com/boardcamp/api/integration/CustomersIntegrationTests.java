package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.factories.CustomersFactory;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.repositories.CustomersRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomersIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomersRepository customersRepository;

    @BeforeEach
    @AfterEach
    public void cleanUpDatabase() {
        customersRepository.deleteAll();
    }

    @Test
    void givenInvalidBody_whenCreatingCustomer_thenThrowsError() {
        CustomerDTO customerDTO = CustomersFactory.createInvalidCustomerDto();

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/customers", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customersRepository.count());
    }

    @Test
    void givenRepeatedCpf_whenCreatingCustomer_thenThrowsError() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        customersRepository.save(customerModel);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/customers", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, customersRepository.count());
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenCreatesCustomer() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customerDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/customers", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, customersRepository.count());
    }

    @Test
    void givenInvalidId_whenFetchingCustomerById_thenThrowsError() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel createdCustomer = customersRepository.save(customerModel);
        customersRepository.deleteById(createdCustomer.getId());

        ResponseEntity<String> response = testRestTemplate.exchange("/customers/{id}", HttpMethod.GET,
                null, String.class, createdCustomer.getId());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenValidId_whenFetchingCustomerById_thenFetchesCustomer() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel createdCustomer = customersRepository.save(customerModel);

        ResponseEntity<String> response = testRestTemplate.exchange("/customers/{id}", HttpMethod.GET,
                null, String.class, createdCustomer.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        String expectedJson = String.format("{\"id\":%d,\"name\":\"%s\",\"cpf\":\"%s\"}", createdCustomer.getId(),
                createdCustomer.getName(), createdCustomer.getCpf());
        assertEquals(expectedJson, response.getBody());
    }
}
