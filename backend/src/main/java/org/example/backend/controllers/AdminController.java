package org.example.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AddBalanceRequest;
import org.example.backend.entities.User;
import org.example.backend.services.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users/{userId}/add-balance")
    public ResponseEntity<Void> addBalance(
            @PathVariable("userId") String userId,
            @RequestBody AddBalanceRequest request) {

        adminService.addBalance(userId, request.amount());
        return ResponseEntity.ok().build();
    }
}
