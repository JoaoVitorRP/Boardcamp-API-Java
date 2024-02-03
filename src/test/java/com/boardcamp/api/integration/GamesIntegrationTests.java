package com.boardcamp.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.mapping.List;
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

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.factories.GamesFactory;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GamesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GamesIntegrationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private GamesRepository gamesRepository;

    @BeforeEach
    @AfterEach
    public void cleanUpDatabase() {
        gamesRepository.deleteAll();
    }

    @Test
    void givenInvalidBody_whenCreatingGame_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateInvalidGameDto();

        HttpEntity<GameDTO> body = new HttpEntity<>(gameDto);

        ResponseEntity<String> response = testRestTemplate.exchange("/games", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, gamesRepository.count());
    }

    @Test
    void givenRepeatedGameName_whenCreatingGame_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);
        GameModel createdGame = gamesRepository.save(gameModel);

        HttpEntity<GameDTO> body = new HttpEntity<>(gameDto);

        ResponseEntity<String> response = testRestTemplate.exchange("/games", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(1, gamesRepository.count());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenCreatesGame() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();

        HttpEntity<GameDTO> body = new HttpEntity<>(gameDto);

        ResponseEntity<String> response = testRestTemplate.exchange("/games", HttpMethod.POST, body, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, gamesRepository.count());
    }

    @Test
    void whenFetchingGames_thenFetchesGames() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/games", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("[]", response.getBody());
    }
}
