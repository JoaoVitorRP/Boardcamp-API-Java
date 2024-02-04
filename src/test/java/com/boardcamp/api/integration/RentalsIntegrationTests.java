package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.factories.CustomersFactory;
import com.boardcamp.api.factories.GamesFactory;
import com.boardcamp.api.factories.RentalsFactory;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomersRepository;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.repositories.RentalsRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RentalsIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RentalsRepository rentalsRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @BeforeEach
    @AfterEach
    public void cleanUpDatabase() {
        rentalsRepository.deleteAll();
        gamesRepository.deleteAll();
        customersRepository.deleteAll();
    }

    private CustomerModel createCustomer() {
        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        CustomerModel customer = customersRepository.save(customerModel);

        return customer;
    }

    private GameModel createGame(boolean noStock) {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();

        if (noStock) {
            gameDto.setStockTotal(0L);
        }

        GameModel gameModel = new GameModel(gameDto);
        GameModel game = gamesRepository.save(gameModel);

        return game;
    }

    private RentalModel createRental(boolean finishedRental) {
        GameModel game = createGame(false);

        CustomerModel customer = createCustomer();

        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);
        Long price = game.getPricePerDay() * rentalDto.getDaysRented();
        RentalModel rentalModel = new RentalModel(rentalDto, price, customer, game);

        if (finishedRental) {
            rentalModel.setReturnDate(LocalDate.now());
        }

        RentalModel rental = rentalsRepository.save(rentalModel);

        return rental;
    }

    @Test
    void givenInvalidBody_whenCreatingRental_thenThrowsError() {
        RentalDTO rentalDTO = RentalsFactory.CreateInvalidRentalDTO();

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalsRepository.count());
    }

    @Test
    void givenInvalidGameId_whenCreatingRental_thenThrowsError() {
        CustomerModel customer = createCustomer();

        RentalDTO rentalDTO = RentalsFactory.CreateValidRentalDTO(customer.getId(), 1L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, rentalsRepository.count());
    }

    @Test
    void givenInvalidCustomerId_whenCreatingRental_thenThrowsError() {
        GameModel game = createGame(false);

        RentalDTO rentalDTO = RentalsFactory.CreateValidRentalDTO(1L, game.getId());

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(0, rentalsRepository.count());
    }

    @Test
    void givenAllGamesAreRented_whenCreatingRental_thenThrowsError() {
        GameModel game = createGame(true);

        CustomerModel customer = createCustomer();

        RentalDTO rentalDTO = RentalsFactory.CreateValidRentalDTO(customer.getId(), game.getId());

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(0, rentalsRepository.count());
    }

    @Test
    void givenValidRental_whenCreatingRental_thenCreatesRental() {
        GameModel game = createGame(false);

        CustomerModel customer = createCustomer();

        RentalDTO rentalDTO = RentalsFactory.CreateValidRentalDTO(customer.getId(), game.getId());

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDTO);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, rentalsRepository.count());
    }

    @Test
    void whenFetchingRentals_thenFetchesRentals() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/rentals", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }

    @Test
    void givenInvalidRentalId_whenUpdatingRental_thenThrowsError() {
        ResponseEntity<String> response = testRestTemplate.exchange("/rentals/{id}/return", HttpMethod.POST, null,
                String.class, 0);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenFinishedRentalId_whenUpdatingRental_thenThrowsError() {
        RentalModel rental = createRental(true);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals/{id}/return", HttpMethod.POST, null,
                String.class,
                rental.getId());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(1, rentalsRepository.count());
    }

    @Test
    void givenRegularRentalId_whenUpdatingRental_thenReturnsRental() {
        RentalModel rental = createRental(false);

        ResponseEntity<String> response = testRestTemplate.exchange("/rentals/{id}/return", HttpMethod.POST, null,
                String.class,
                rental.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, rentalsRepository.count());
    }
}
