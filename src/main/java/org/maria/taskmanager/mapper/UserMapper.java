package org.maria.taskmanager.mapper;

import lombok.RequiredArgsConstructor;
import org.maria.taskmanager.dto.request.PostRegisterRequestDto;
import org.maria.taskmanager.model.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toUser(PostRegisterRequestDto request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }
}
