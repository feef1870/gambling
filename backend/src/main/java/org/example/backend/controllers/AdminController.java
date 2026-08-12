package org.example.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AddBalanceRequest;
import org.example.backend.dto.UserResponse;
import org.example.backend.services.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        Page<UserResponse> response = adminService.getUsers(search, page, size)
                .map(UserResponse::from);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/add-balance")
    public ResponseEntity<Void> addBalance(
            @PathVariable("userId") String userId,
            @RequestBody AddBalanceRequest request) {

        adminService.addBalance(userId, request.amount());
        return ResponseEntity.ok().build();
    }
}
