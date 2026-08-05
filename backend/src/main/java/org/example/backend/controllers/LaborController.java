package org.example.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.backend.services.LaborService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/labor")
@RequiredArgsConstructor
public class LaborController {
    private final LaborService laborService;

    @PostMapping("/claim")
    public ResponseEntity<Void> claimWage(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();

        laborService.payWage(userId);

        return ResponseEntity.ok().build();
    }
}
