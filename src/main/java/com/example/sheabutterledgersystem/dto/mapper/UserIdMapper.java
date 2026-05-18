package com.example.sheabutterledgersystem.dto.mapper;

import com.example.sheabutterledgersystem.model.User;
import com.example.sheabutterledgersystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserIdMapper {

    @Autowired
    private UserRepository userRepository;

    @Named("uuidToUser")
    public User uuidToUser(UUID userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Named("userToUuid")
    public UUID userToUuid(User user) {
        return user != null ? user.getId() : null;
    }
}