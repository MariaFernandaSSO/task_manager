package org.maria.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.maria.taskmanager.TestcontainersConfiguration;
import org.maria.taskmanager.dto.request.PostLoginRequestDto;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("AuthController — Integration")
class AuthControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /auth/register — deve registrar usuário e retornar token")
    void register_success() throws Exception {
        PostRegisterRequestDto request = new PostRegisterRequestDto();
        request.setName("Maria");
        request.setEmail("maria@email.com");
        request.setPassword("senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.name").value("Maria"));
    }

    @Test
    @DisplayName("POST /auth/register — deve retornar 400 quando campos obrigatórios ausentes")
    void register_validationError() throws Exception {
        PostRegisterRequestDto request = new PostRegisterRequestDto();
        request.setEmail("email-invalido");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").exists());
    }

    @Test
    @DisplayName("POST /auth/register — deve retornar erro quando e-mail já existe")
    void register_duplicateEmail() throws Exception {
        PostRegisterRequestDto request = new PostRegisterRequestDto();
        request.setName("Maria");
        request.setEmail("duplicado@email.com");
        request.setPassword("senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("duplicado@email.com")));
    }

    @Test
    @DisplayName("POST /auth/login — deve autenticar e retornar token")
    void login_success() throws Exception {
        PostRegisterRequestDto registerRequest = new PostRegisterRequestDto();
        registerRequest.setName("Maria");
        registerRequest.setEmail("login@email.com");
        registerRequest.setPassword("senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        PostLoginRequestDto loginRequest = new PostLoginRequestDto();
        loginRequest.setEmail("login@email.com");
        loginRequest.setPassword("senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /auth/login — deve retornar 401 com credenciais inválidas")
    void login_invalidCredentials() throws Exception {
        PostLoginRequestDto loginRequest = new PostLoginRequestDto();
        loginRequest.setEmail("naoexiste@email.com");
        loginRequest.setPassword("senhaerrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}

