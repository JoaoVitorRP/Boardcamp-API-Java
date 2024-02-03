package com.boardcamp.api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.errors.ConflictException;
import com.boardcamp.api.factories.GamesFactory;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GamesRepository;
import com.boardcamp.api.services.GamesService;

@SpringBootTest
class GamesUnitTests {
    @InjectMocks
    private GamesService gamesService;

    @Mock
    private GamesRepository gamesRepository;

    @Test
    void givenRepeatedGameName_whenCreatingGame_thenThrowsError() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();

        doReturn(true).when(gamesRepository).existsByName(any());

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> gamesService.save(gameDto));

        verify(gamesRepository, times(1)).existsByName(any());
        verify(gamesRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Game already exists with the given name!", exception.getMessage());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenCreatesGame() {
        GameDTO gameDto = GamesFactory.CreateValidGameDTO();
        GameModel gameModel = new GameModel(gameDto);

        doReturn(false).when(gamesRepository).existsByName(any());
        doReturn(gameModel).when(gamesRepository).save(any());

        GameModel result = gamesService.save(gameDto);

        verify(gamesRepository, times(1)).existsByName(any());
        verify(gamesRepository, times(1)).save(any());
        assertEquals(gameModel, result);
    }
}
