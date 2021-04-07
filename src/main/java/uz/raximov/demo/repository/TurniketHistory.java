package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TurniketHistory extends JpaRepository<TurniketHistory, UUID> {
}
