package org.maria.taskmanager.dto.request;

import lombok.Data;
import org.maria.taskmanager.model.task.TaskPriority;
import org.maria.taskmanager.model.task.TaskStatus;

import java.time.LocalDateTime;

@Data
public class PatchTaskRequestDto {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime dueDate;
}

