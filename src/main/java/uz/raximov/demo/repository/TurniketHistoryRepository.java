package uz.raximov.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.TurniketHistory;

import java.util.UUID;

public interface TurniketHistoryRepository extends JpaRepository<TurniketHistory, UUID> {
}
