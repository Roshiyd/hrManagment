package uz.raximov.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.raximov.demo.entity.template.AbsEntity;
import uz.raximov.demo.enums.TaskStatus;

import javax.persistence.*;
import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Task extends AbsEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private Timestamp deadline;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.STATUS_NEW;

    @ManyToOne(optional = false)
    private User taskTaker;//qabul qiluvchi

    @ManyToOne(optional = false)
    private User taskGiver;//vazifa beruvchi
}
