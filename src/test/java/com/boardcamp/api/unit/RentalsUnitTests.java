package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.errors.NotFoundException;
import com.boardcamp.api.errors.UnprocessableEntityException;
import com.boardcamp.api.factories.CustomersFactory;
import com.boardcamp.api.factories.GamesFactory;
import com.boardcamp.api.factories.RentalsFactory;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomersRepository;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.repositories.RentalsRepository;
import com.boardcamp.api.services.RentalsService;

@SpringBootTest
class RentalsUnitTests {
    @InjectMocks
    private RentalsService rentalsService;

    @Mock
    private RentalsRepository rentalsRepository;

    @Mock
    private GamesRepository gamesRepository;

    @Mock
    private CustomersRepository customersRepository;

    @Test
    void givenInvalidGameId_whenCreatingRental_thenThrowsError() {
        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);

        doReturn(Optional.empty()).when(gamesRepository).findById(any());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> rentalsService.save(rentalDto));

        verify(gamesRepository, times(1)).findById(any());
        verify(customersRepository, times(0)).findById(any());
        verify(rentalsRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Game not found!", exception.getMessage());
    }

    @Test
    void givenInvalidCustomerId_whenCreatingRental_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);

        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);

        doReturn(Optional.of(gameModel)).when(gamesRepository).findById(any());
        doReturn(Optional.empty()).when(customersRepository).findById(any());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> rentalsService.save(rentalDto));

        verify(gamesRepository, times(1)).findById(any());
        verify(customersRepository, times(1)).findById(any());
        verify(rentalsRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Customer not found!", exception.getMessage());
    }

    @Test
    void givenAllGamesAreRented_whenCreatingRental_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);

        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);

        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);

        doReturn(Optional.of(gameModel)).when(gamesRepository).findById(any());
        doReturn(Optional.of(customerModel)).when(customersRepository).findById(any());
        doReturn(gameDto.getStockTotal()).when(rentalsRepository).countRentedGamesById(any());

        UnprocessableEntityException exception = assertThrows(
                UnprocessableEntityException.class,
                () -> rentalsService.save(rentalDto));

        verify(gamesRepository, times(1)).findById(any());
        verify(customersRepository, times(1)).findById(any());
        verify(rentalsRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Out of stock for game with given id!", exception.getMessage());
    }

    @Test
    void givenValidRental_whenCreatingRental_thenCreatesRental() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);
        gameModel.setId(1L);

        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        customerModel.setId(1L);

        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);
        Long price = gameModel.getPricePerDay() * rentalDto.getDaysRented();
        RentalModel rentalModel = new RentalModel(rentalDto, price, customerModel, gameModel);

        doReturn(Optional.of(gameModel)).when(gamesRepository).findById(any());
        doReturn(Optional.of(customerModel)).when(customersRepository).findById(any());
        doReturn(0L).when(rentalsRepository).countRentedGamesById(any());
        doReturn(rentalModel).when(rentalsRepository).save(any());

        RentalModel result = rentalsService.save(rentalDto);

        verify(gamesRepository, times(1)).findById(any());
        verify(customersRepository, times(1)).findById(any());
        verify(rentalsRepository, times(1)).save(any());
        assertEquals(price, result.getOriginalPrice());
        assertEquals(LocalDate.now(), result.getRentDate());
        assertEquals(0, result.getDelayFee());
        assertEquals(rentalModel, result);
    }

    @Test
    void givenInvalidRentalId_whenUpdatingRental_thenThrowsError() {
        doReturn(Optional.empty()).when(rentalsRepository).findById(any());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> rentalsService.update(1L));

        verify(rentalsRepository, times(1)).findById(any());
        verify(rentalsRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Rental not found!", exception.getMessage());
    }

    @Test
    void givenFinishedRentalId_whenUpdatingRental_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);

        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);

        RentalDTO rentalDto = RentalsFactory.CreateValidRentalDTO(1L, 1L);
        Long price = gameModel.getPricePerDay() * rentalDto.getDaysRented();
        RentalModel rentalModel = new RentalModel(rentalDto, price, customerModel, gameModel);
        rentalModel.setReturnDate(LocalDate.now());

        doReturn(Optional.of(rentalModel)).when(rentalsRepository).findById(any());

        UnprocessableEntityException exception = assertThrows(
                UnprocessableEntityException.class,
                () -> rentalsService.update(1L));

        verify(rentalsRepository, times(1)).findById(any());
        verify(rentalsRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Rental has already been returned!", exception.getMessage());
    }

    @Test
    void givenRegularRentalId_whenUpdatingRental_thenReturnsRental() {
        RentalModel rentalModel = RentalsFactory.CreateValidRentalModel();

        doReturn(Optional.of(rentalModel)).when(rentalsRepository).findById(any());
        doReturn(rentalModel).when(rentalsRepository).save(any());
        
        RentalModel result = rentalsService.update(rentalModel.getId());

        verify(rentalsRepository, times(1)).findById(any());
        verify(rentalsRepository, times(1)).save(any());
        assertEquals(rentalModel.getOriginalPrice(), result.getOriginalPrice());
        assertEquals(0, result.getDelayFee());
        assertEquals(LocalDate.now(), result.getReturnDate());
        assertEquals(rentalModel, result);
    }

    @Test
    void givenExpiredRentalId_whenUpdatingRental_thenAddsFee() {
        RentalModel rentalModel = RentalsFactory.CreateValidRentalModel();
        Long daysRented = rentalModel.getDaysRented();
        rentalModel.setRentDate(LocalDate.now().minusDays(daysRented + 5));

        Long daysPassed = ChronoUnit.DAYS.between(rentalModel.getRentDate(), LocalDate.now());
        Long delayFee = (daysPassed - daysRented) * rentalModel.getGame().getPricePerDay();

        doReturn(Optional.of(rentalModel)).when(rentalsRepository).findById(any());
        doReturn(rentalModel).when(rentalsRepository).save(any());

        RentalModel result = rentalsService.update(rentalModel.getId());

        verify(rentalsRepository, times(1)).findById(any());
        verify(rentalsRepository, times(1)).save(any());
        assertEquals(rentalModel.getOriginalPrice(), result.getOriginalPrice());
        assertEquals(delayFee, result.getDelayFee());
        assertEquals(LocalDate.now(), result.getReturnDate());
        assertEquals(rentalModel, result);
    }
}
