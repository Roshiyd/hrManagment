package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.SalaryTaken;

import java.util.UUID;

public interface SalaryTakenRepository extends JpaRepository<SalaryTaken, UUID> {
}
