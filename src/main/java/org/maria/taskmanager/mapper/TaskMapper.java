package org.maria.taskmanager.mapper;

import org.maria.taskmanager.dto.request.PatchTaskRequestDto;
import org.maria.taskmanager.dto.request.PostTaskRequestDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskPageableDto;
import org.maria.taskmanager.model.task.Task;
import org.maria.taskmanager.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class TaskMapper {

    public Task toTask(User user, PostTaskRequestDto request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .user(user)
                .build();
    }

    public void updateTaskFromDto(PatchTaskRequestDto dto, Task task) {
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
    }

    public ResponseTaskDto toResponse(Task task) {
        return ResponseTaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate())
                .userId(task.getUser().getId())
                .build();
    }

    public ResponseTaskPageableDto toTaskResponsePageableDto(int page, int pageSize, Page<Task> taskPage) {

        var tasks = taskPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return ResponseTaskPageableDto.builder()
                .page(page)
                .pageSize(pageSize)
                .totalElements(taskPage.getTotalElements())
                .tasks(tasks)
                .build();
    }
}
