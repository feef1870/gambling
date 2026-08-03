package org.example.backend.repositories;

import jakarta.persistence.LockModeType;
import org.example.backend.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    Optional<Game> findByIdWithLock(@Param("id")  Long id);
}
