package com.boardcamp.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.services.GamesService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/games")
public class GamesController {
    final GamesService gamesService;

    GamesController(GamesService gamesService) {
        this.gamesService = gamesService;
    }

    @PostMapping
    public ResponseEntity<GameModel> postGame(@RequestBody @Valid GameDTO body) {
        GameModel game = gamesService.save(body);

        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> getAllGames() {
        List<GameModel> games = gamesService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

}
