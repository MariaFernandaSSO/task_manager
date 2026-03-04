package org.maria.taskmanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.maria.taskmanager.model.task.TaskPriority;
import org.maria.taskmanager.model.task.TaskStatus;

import java.time.LocalDateTime;

@Data
public class PostTaskRequestDto {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Status é obrigatório")
    private TaskStatus status;

    @NotNull(message = "Prioridade é obrigatória")
    private TaskPriority priority;

    private LocalDateTime dueDate;
}

