package org.maria.taskmanager.repository;

import org.maria.taskmanager.model.task.Task;
import org.maria.taskmanager.model.task.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, String> {

    @Query("SELECT t FROM Task t "
            + "JOIN t.user u "
            + "WHERE u.id = :userId "
            + "AND (:status IS NULL OR t.status IN :status) "
            + "AND t.isDeleted = false"
    )
    Page<Task> findAllByUserIdAndStatus(Long userId, List<TaskStatus> status, Pageable pageable);

    @Query("SELECT t FROM Task t "
            + "JOIN t.user u "
            + "WHERE t.id = :id "
            + "AND u.id = :userId "
            + "AND t.isDeleted = false"
    )
    Optional<Task> findByIdAndUserId(String id, Long userId);
}

