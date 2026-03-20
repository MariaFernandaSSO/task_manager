package org.maria.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maria.taskmanager.dto.request.PostLoginRequestDto;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.maria.taskmanager.dto.response.auth.ResponseAuthDto;
import org.maria.taskmanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints de registro e login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar novo usuário")
    public ResponseAuthDto register(@Valid @RequestBody PostRegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Autenticar usuário e obter token JWT")
    public ResponseAuthDto login(@Valid @RequestBody PostLoginRequestDto request) {
        return authService.login(request);
    }
}

