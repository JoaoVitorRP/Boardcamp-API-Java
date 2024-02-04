package com.boardcamp.api.factories;

import com.boardcamp.api.dtos.CustomerDTO;
import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;

public class RentalsFactory {
    public static RentalDTO CreateValidRentalDTO(Long customerId, Long gameId) {
        RentalDTO rental = new RentalDTO(customerId, gameId, 4L);

        return rental;
    }

    public static RentalModel CreateValidRentalModel() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);
        gameModel.setId(1L);

        CustomerDTO customerDTO = CustomersFactory.createValidCustomerDto();
        CustomerModel customerModel = new CustomerModel(customerDTO);
        customerModel.setId(1L);

        RentalDTO rentalDto = CreateValidRentalDTO(1L, 1L);
        Long price = gameModel.getPricePerDay() * rentalDto.getDaysRented();
        RentalModel rentalModel = new RentalModel(rentalDto, price, customerModel, gameModel);
        rentalModel.setId(1L);

        return rentalModel;
    }
}
