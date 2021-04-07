package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Integer> {

}
