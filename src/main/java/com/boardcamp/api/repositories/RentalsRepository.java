package com.boardcamp.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcamp.api.models.RentalModel;

@Repository
public interface RentalsRepository extends JpaRepository<RentalModel, Long> {
    Long countByGameId(Long gameId);
}
