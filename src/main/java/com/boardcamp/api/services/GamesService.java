package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.errors.ConflictException;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GamesRepository;

@Service
public class GamesService {
    final GamesRepository gamesRepository;

    GamesService(GamesRepository gamesRepository) {
        this.gamesRepository = gamesRepository;
    }

    public GameModel save(GameDTO dto) {
        if (gamesRepository.existsByName(dto.getName())) {
            throw new ConflictException("Game already exists with the given name!");
        }

        GameModel game = new GameModel(dto);
        return gamesRepository.save(game);
    }

    public List<GameModel> findAll() {
        return gamesRepository.findAll();
    }
}
