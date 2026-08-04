package org.example.backend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.GameActionRequest;
import org.example.backend.dto.GameCreateRequest;
import org.example.backend.dto.GameResponse;
import org.example.backend.entities.Game;
import org.example.backend.services.GameService;
import org.example.backend.util.GameMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> startGame(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody GameCreateRequest request) {

        String userId = jwt.getSubject();
        Game game = gameService.startGame(userId, request.betAmount());

        String comment = gameService.getAiCommentForGame(game);

        return ResponseEntity.ok(GameMapper.toResponse(game, comment));
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<GameResponse> processAction(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long gameId,
            @Valid @RequestBody GameActionRequest request) {

        String userId = jwt.getSubject();
        Game game = gameService.processPlayerAction(gameId, userId, request.action());

        String comment = gameService.getAiCommentForGame(game);

        return ResponseEntity.ok(GameMapper.toResponse(game, comment));
    }
}
