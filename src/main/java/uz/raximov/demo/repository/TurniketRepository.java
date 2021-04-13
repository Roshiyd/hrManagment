package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Turniket;

import java.util.Optional;
import java.util.UUID;

public interface TurniketRepository extends JpaRepository<Turniket, UUID> {
    Optional<Turniket> findByNumber(String number);
}
