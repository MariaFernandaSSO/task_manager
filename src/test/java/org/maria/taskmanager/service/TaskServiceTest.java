package org.maria.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maria.taskmanager.dto.request.PatchTaskRequestDto;
import org.maria.taskmanager.dto.request.PostTaskRequestDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskDto;
import org.maria.taskmanager.dto.response.task.ResponseTaskPageableDto;
import org.maria.taskmanager.exception.BusinessException;
import org.maria.taskmanager.exception.ResourceNotFoundException;
import org.maria.taskmanager.mapper.TaskMapper;
import org.maria.taskmanager.model.task.Task;
import org.maria.taskmanager.model.task.TaskPriority;
import org.maria.taskmanager.model.task.TaskStatus;
import org.maria.taskmanager.model.user.User;
import org.maria.taskmanager.repository.TaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService")
class TaskServiceTest {

    @Mock private TaskMapper taskMapper;
    @Mock private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private ResponseTaskDto responseTaskDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Maria")
                .email("maria@email.com")
                .password("encoded_password")
                .build();

        task = Task.builder()
                .id("uuid-123")
                .title("Estudar Java")
                .description("Estudar Spring Boot")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        responseTaskDto = ResponseTaskDto.builder()
                .id("uuid-123")
                .title("Estudar Java")
                .description("Estudar Spring Boot")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .userId(1L)
                .build();
    }

    @Test
    @DisplayName("deve criar tarefa com sucesso")
    void create_success() {
        PostTaskRequestDto request = new PostTaskRequestDto();
        request.setTitle("Estudar Java");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);

        given(taskMapper.toTask(user, request)).willReturn(task);
        given(taskMapper.toResponse(task)).willReturn(responseTaskDto);

        ResponseTaskDto response = taskService.create(request, user);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Estudar Java");
        assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("deve lançar BusinessException quando pageSize excede 100")
    void findAllTasks_pageSizeExceeds100() {
        assertThatThrownBy(() -> taskService.findAllTasks(0, 101, user, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("100");
    }

    @Test
    @DisplayName("deve listar tarefas paginadas com sucesso")
    void findAllTasks_success() {
        var page = new PageImpl<>(List.of(task), PageRequest.of(0, 10), 1);
        var expectedResponse = ResponseTaskPageableDto.builder()
                .page(0).pageSize(10).totalElements(1L).tasks(List.of(responseTaskDto)).build();

        given(taskRepository.findAllByUserIdAndStatus(eq(user.getId()), any(), any())).willReturn(page);
        given(taskMapper.toTaskResponsePageableDto(0, 10, page)).willReturn(expectedResponse);

        ResponseTaskPageableDto response = taskService.findAllTasks(0, 10, user, null);

        assertThat(response.getTotalElements()).isEqualTo(1L);
        assertThat(response.getTasks()).hasSize(1);
    }

    @Test
    @DisplayName("deve buscar tarefa por ID com sucesso")
    void findById_success() {
        given(taskRepository.findByIdAndUserId("uuid-123", 1L)).willReturn(Optional.of(task));
        given(taskMapper.toResponse(task)).willReturn(responseTaskDto);

        ResponseTaskDto response = taskService.findById(user, "uuid-123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("uuid-123");
    }

    @Test
    @DisplayName("deve lançar ResourceNotFoundException quando tarefa não existe")
    void findById_notFound() {
        given(taskRepository.findByIdAndUserId("id-invalido", 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(user, "id-invalido"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id-invalido");
    }

    @Test
    @DisplayName("deve atualizar tarefa com sucesso")
    void update_success() {
        PatchTaskRequestDto patchRequest = new PatchTaskRequestDto();
        patchRequest.setTitle("Novo título");

        given(taskRepository.findByIdAndUserId("uuid-123", 1L)).willReturn(Optional.of(task));
        given(taskRepository.save(task)).willReturn(task);
        given(taskMapper.toResponse(task)).willReturn(responseTaskDto);

        ResponseTaskDto response = taskService.update(user, "uuid-123", patchRequest);

        assertThat(response).isNotNull();
        verify(taskMapper).updateTaskFromDto(patchRequest, task);
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("deve lançar ResourceNotFoundException ao atualizar tarefa inexistente")
    void update_notFound() {
        given(taskRepository.findByIdAndUserId("id-invalido", 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(user, "id-invalido", new PatchTaskRequestDto()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deve deletar tarefa com sucesso")
    void delete_success() {
        given(taskRepository.findByIdAndUserId("uuid-123", 1L)).willReturn(Optional.of(task));

        taskService.delete("uuid-123", user);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("deve lançar ResourceNotFoundException ao deletar tarefa inexistente")
    void delete_notFound() {
        given(taskRepository.findByIdAndUserId("id-invalido", 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete("id-invalido", user))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

