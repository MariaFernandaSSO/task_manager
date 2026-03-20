package org.maria.taskmanager.service;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maria.taskmanager.dto.request.PostLoginRequestDto;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.maria.taskmanager.dto.response.auth.ResponseAuthDto;
import org.maria.taskmanager.exception.BusinessException;
import org.maria.taskmanager.mapper.AuthMapper;
import org.maria.taskmanager.mapper.UserMapper;
import org.maria.taskmanager.model.user.User;
import org.maria.taskmanager.repository.UserRepository;
import org.maria.taskmanager.security.JwtUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User user;
    private PostRegisterRequestDto registerRequest;
    private PostLoginRequestDto loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Maria")
                .email("maria@email.com")
                .password("encoded_password")
                .build();

        registerRequest = new PostRegisterRequestDto();
        registerRequest.setName("Maria");
        registerRequest.setEmail("maria@email.com");
        registerRequest.setPassword("senha123");

        loginRequest = new PostLoginRequestDto();
        loginRequest.setEmail("maria@email.com");
        loginRequest.setPassword("senha123");
    }

    @Test
    @DisplayName("deve registrar usuário com sucesso")
    void register_success() {
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(false);
        given(userMapper.toUser(registerRequest)).willReturn(user);
        given(authMapper.toAuthResponse(nullable(String.class), eq(user)))
                .willReturn(new ResponseAuthDto(null, "maria@email.com", "Maria"));

        ResponseAuthDto response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNull();
        assertThat(response.getEmail()).isEqualTo("maria@email.com");
        verify(userRepository).save(user);
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("deve lançar BusinessException quando e-mail já está cadastrado")
    void register_emailAlreadyExists() {
        given(userRepository.existsByEmail(registerRequest.getEmail())).willReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("E-mail já cadastrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve realizar login com sucesso")
    void login_success() {
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.of(user));
        given(jwtUtil.generateToken(user)).willReturn("jwt_token");
        given(authMapper.toAuthResponse(nullable(String.class), eq(user)))
                .willReturn(new ResponseAuthDto("jwt_token", "maria@email.com", "Maria"));

        ResponseAuthDto response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt_token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("deve lançar BusinessException quando usuário não é encontrado no login")
    void login_userNotFound() {
        given(userRepository.findByEmail(loginRequest.getEmail())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
}
