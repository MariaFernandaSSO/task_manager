package org.maria.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.maria.taskmanager.dto.request.PatchTaskRequestDto;
import org.maria.taskmanager.dto.request.PostTaskRequestDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskPageableDto;
import org.maria.taskmanager.model.task.TaskStatus;
import org.maria.taskmanager.model.user.User;
import org.maria.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tarefas", description = "CRUD de tarefas do usuário autenticado")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Criar nova tarefa")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseTaskDto create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PostTaskRequestDto request) {

        return taskService.create(request, user);
    }

    @GetMapping
    @Operation(summary = "Listar todas as tarefas do usuário")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTaskPageableDto findAllTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, name = "page", defaultValue = "0") int page,
            @RequestParam(required = false, name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(required = false) List<TaskStatus> status) {
       return taskService.findAllTasks(page, pageSize, user, status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTaskDto findById(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {
        return taskService.findById(user, id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar tarefa parcialmente")
    @ResponseStatus(HttpStatus.OK)
    public ResponseTaskDto update(
            @AuthenticationPrincipal User user,
            @PathVariable String id,
            @RequestBody PatchTaskRequestDto request) {

        return taskService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar tarefa")
    public void delete(
            @AuthenticationPrincipal User user,
            @PathVariable String id) {
        taskService.delete(id, user);
    }
}

