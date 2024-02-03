package com.boardcamp.api.factories;

import com.boardcamp.api.dtos.GameDTO;

public class GamesFactory {
    public static GameDTO CreateValidGameDTO() {
        GameDTO game = new GameDTO("Jogo da Vida", "https://", 3L, 5000L);

        return game;
    }

    public static GameDTO CreateInvalidGameDto() {
        GameDTO game = new GameDTO("", "https://", 3L, 5000L);

        return game;
    }
}
