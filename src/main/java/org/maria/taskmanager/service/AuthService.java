package org.maria.taskmanager.service;

import lombok.AllArgsConstructor;
import org.maria.taskmanager.dto.request.PostLoginRequestDto;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.maria.taskmanager.dto.response.auth.ResponseAuthDto;
import org.maria.taskmanager.exception.BusinessException;
import org.maria.taskmanager.mapper.AuthMapper;
import org.maria.taskmanager.mapper.UserMapper;
import org.maria.taskmanager.model.user.User;
import org.maria.taskmanager.repository.UserRepository;
import org.maria.taskmanager.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final JwtUtil jwtUtil;

    public ResponseAuthDto register(PostRegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado: " + request.getEmail());
        }

        User user = userMapper.toUser(request);
        userRepository.save(user);

        return authMapper.toAuthResponse(null, user);
    }

    public ResponseAuthDto login(PostLoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(user);

        return authMapper.toAuthResponse(token, user);
    }
}
