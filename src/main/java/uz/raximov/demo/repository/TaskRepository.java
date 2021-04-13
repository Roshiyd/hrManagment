package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Task;
import uz.raximov.demo.entity.User;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByTaskTaker(User taskTaker);
    List<Task> findByTaskGiver(User taskTaker);
    List<Task> findByTaskTakerAndIdNot(User taskTaker, UUID id);

}
