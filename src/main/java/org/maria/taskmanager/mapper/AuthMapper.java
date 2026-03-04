package org.maria.taskmanager.mapper;

import org.maria.taskmanager.dto.response.auth.ResponseAuthDto;
import org.maria.taskmanager.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class AuthMapper {

    public ResponseAuthDto toAuthResponse(String token, User user) {
        return ResponseAuthDto.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
