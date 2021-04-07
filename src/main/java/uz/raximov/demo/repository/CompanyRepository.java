package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
