package uz.raximov.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Turniket;
import uz.raximov.demo.entity.TurniketHistory;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface TurniketHistoryRepository extends JpaRepository<TurniketHistory, UUID> {
    List<TurniketHistory> findAllByTurniketAndTimeIsBetween(Turniket turniket, Timestamp time, Timestamp time2);
    List<TurniketHistory> findAllByTurniket(Turniket turniket);
}
