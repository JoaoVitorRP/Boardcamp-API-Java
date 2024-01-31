package com.boardcamp.api.services;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.errors.NotFoundException;
import com.boardcamp.api.errors.UnprocessableEntityException;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomersRepository;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.repositories.RentalsRepository;

@Service
public class RentalsService {
    final RentalsRepository rentalsRepository;
    final GamesRepository gamesRepository;
    final CustomersRepository customersRepository;

    RentalsService(RentalsRepository rentalsRepository, GamesRepository gamesRepository,
            CustomersRepository customersRepository) {
        this.rentalsRepository = rentalsRepository;
        this.gamesRepository = gamesRepository;
        this.customersRepository = customersRepository;
    }

    public RentalModel save(RentalDTO dto) {
        GameModel game = gamesRepository.findById(dto.getGameId())
                .orElseThrow(() -> new NotFoundException("Game not found!"));

        CustomerModel customer = customersRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        Long rentalCount = rentalsRepository.countByGameId(dto.getGameId());
        if (rentalCount >= game.getStockTotal()) {
            throw new UnprocessableEntityException("Out of stock for game with given id!");
        }

        Long price = game.getPricePerDay() * dto.getDaysRented();

        RentalModel rental = new RentalModel(dto, price, customer, game);
        return rentalsRepository.save(rental);
    }
}
