package uz.raximov.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.raximov.demo.entity.Task;
import uz.raximov.demo.entity.User;
import uz.raximov.demo.enums.TaskStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByTaskTaker(User taskTaker);
    List<Task> findAllByTaskGiver(User taskTaker);
    List<Task> findByTaskTakerAndIdNot(User taskTaker, UUID id);
    List<Task> findAllByTaskGiverAndCreatedAtBetweenAndStatus(User taskGiver, Timestamp startTime, Timestamp endTime, TaskStatus status);
}
