package org.example.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.UserResponse;
import org.example.backend.entities.User;
import org.example.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getCurrentUser(jwt.getSubject());
        return ResponseEntity.ok(UserResponse.from(user));
    }

}
