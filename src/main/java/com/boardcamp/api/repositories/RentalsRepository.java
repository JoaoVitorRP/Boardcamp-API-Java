package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.RentalModel;

@Repository
public interface RentalsRepository extends JpaRepository<RentalModel, Long> {
    @Query(value = "SELECT COUNT(game_id) FROM rentals WHERE return_date IS NULL AND game_id=?", nativeQuery = true)
    Long countRentedGamesById(@Param("gameId") Long gameId);
}
