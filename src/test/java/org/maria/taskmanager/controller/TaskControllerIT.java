package org.maria.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.maria.taskmanager.TestcontainersConfiguration;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.maria.taskmanager.dto.request.PostTaskRequestDto;
import org.maria.taskmanager.dto.request.PatchTaskRequestDto;
import org.maria.taskmanager.model.task.TaskPriority;
import org.maria.taskmanager.model.task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("TaskController — Integration")
class TaskControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        PostRegisterRequestDto register = new PostRegisterRequestDto();
        register.setName("Maria");
        register.setEmail("maria@email.com");
        register.setPassword("senha123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated())
                .andReturn();

        token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    private PostTaskRequestDto buildTaskRequest(String title) {
        PostTaskRequestDto request = new PostTaskRequestDto();
        request.setTitle(title);
        request.setDescription("Descrição da tarefa");
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.HIGH);
        return request;
    }

    private String createTask(String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTaskRequest(title))))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }

    @Test
    @DisplayName("POST /tasks — deve criar tarefa com sucesso")
    void create_success() throws Exception {
        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTaskRequest("Estudar Java"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Estudar Java"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("POST /tasks — deve retornar 401 sem token")
    void create_unauthorized() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildTaskRequest("Tarefa"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /tasks — deve retornar 400 quando título ausente")
    void create_validationError() throws Exception {
        PostTaskRequestDto request = new PostTaskRequestDto();
        request.setStatus(TaskStatus.TODO);
        request.setPriority(TaskPriority.MEDIUM);

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title").exists());
    }

    @Test
    @DisplayName("GET /tasks — deve listar tarefas paginadas")
    void findAll_success() throws Exception {
        createTask("Tarefa 1");
        createTask("Tarefa 2");

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /tasks — deve retornar 422 quando pageSize excede 100")
    void findAll_pageSizeTooLarge() throws Exception {
        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("pageSize", "101"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /tasks/{id} — deve buscar tarefa por ID")
    void findById_success() throws Exception {
        String id = createTask("Tarefa por ID");

        mockMvc.perform(get("/tasks/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Tarefa por ID"));
    }

    @Test
    @DisplayName("GET /tasks/{id} — deve retornar 404 para ID inexistente")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", "id-que-nao-existe")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /tasks/{id} — deve atualizar tarefa parcialmente")
    void update_success() throws Exception {
        String id = createTask("Título original");

        PatchTaskRequestDto patch = new PatchTaskRequestDto();
        patch.setTitle("Título atualizado");
        patch.setStatus(TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/tasks/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título atualizado"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("DELETE /tasks/{id} — deve deletar tarefa com sucesso")
    void delete_success() throws Exception {
        String id = createTask("Tarefa para deletar");

        mockMvc.perform(delete("/tasks/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /tasks/{id} — deve retornar 404 para tarefa inexistente")
    void delete_notFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", "id-inexistente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}

