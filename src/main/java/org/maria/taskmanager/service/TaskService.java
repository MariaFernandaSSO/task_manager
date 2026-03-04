package org.maria.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.maria.taskmanager.dto.request.PatchTaskRequestDto;
import org.maria.taskmanager.dto.request.PostTaskRequestDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskPageableDto;
import org.maria.taskmanager.exception.BusinessException;
import org.maria.taskmanager.exception.ResourceNotFoundException;
import org.maria.taskmanager.mapper.TaskMapper;
import org.maria.taskmanager.model.task.Task;
import org.maria.taskmanager.model.task.TaskStatus;
import org.maria.taskmanager.model.user.User;
import org.maria.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public ResponseTaskDto create(PostTaskRequestDto request, User user) {

        var task = taskMapper.toTask(user, request);
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public ResponseTaskPageableDto findAllTasks(int page, int pageSize, User user, List<TaskStatus> status) {
        if (pageSize > 100)
            throw new BusinessException("Não é possível solicitar mais de 100 tarefas por página");

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Task> taskPage = taskRepository.findAllByUserIdAndStatus(user.getId(), status, pageable);

        return this.taskMapper.toTaskResponsePageableDto(page, pageSize, taskPage);
    }

    public ResponseTaskDto findById(User user, String id) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com id: " + id + " não encontrada"));

        return taskMapper.toResponse(task);
    }

    public ResponseTaskDto update(User user, String id, PatchTaskRequestDto requestDto) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com id: " + id + " não encontrada"));

        taskMapper.updateTaskFromDto(requestDto, task);
        taskRepository.save(task);

        return taskMapper.toResponse(task);
    }

    public void delete(String id, User user) {
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com id: " + id + " não encontrada"));

        task.setDeleted(true);

        taskRepository.save(task);
    }
}

