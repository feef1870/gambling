package org.example.backend.repositories;

import org.example.backend.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
