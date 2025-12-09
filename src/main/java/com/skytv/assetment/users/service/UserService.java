package com.skytv.assetment.users.service;


import com.skytv.assetment.users.dto.*;
import com.skytv.assetment.users.exception.UserDuplicatedException;
import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import com.skytv.assetment.users.exception.ResourceNotFoundException;
import com.skytv.assetment.users.repository.ExternalProjectRepository;
import com.skytv.assetment.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ExternalProjectRepository externalProjectRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest request) {

        log.info("Creating user: {}", request);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();

        userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            log.error("User with email {} already exists", user.getEmail());
            throw new UserDuplicatedException("User with email %s already exists".formatted(user.getEmail()));
        });

        User saved = userRepository.save(user);
        log.info("Created user: {}", saved);
        return toUserResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        log.info("Fetching user: {}", id);
        User user = findUserOrThrow(id);
        log.info("User fetched: {}", user);
        return toUserResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user: {}", id);
        User user = findUserOrThrow(id);
        log.info("User fetched: {}", user);

        user.setEmail(request.email());
        user.setName(request.name());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return toUserResponse(user);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    public ExternalProjectResponse addExternalProject(Long userId, ExternalProjectRequest request) {
        log.info("Adding external project to user {}", userId);
        User user = findUserOrThrow(userId);

        ExternalProject project = null;
        if(StringUtils.isBlank(request.id()) ) {
            project = ExternalProject.builder()
                    .name(request.name())
                    .build();
        } else {
            project = externalProjectRepository.findById(request.id())
                    .orElseThrow(() -> new ResourceNotFoundException("External Project %s not found".formatted(request.id())));
        }
        project.addUser(user);



        ExternalProject saved = externalProjectRepository.save(project);
        log.info("Added external project to user {}", userId);
        return new ExternalProjectResponse(saved.getId(), saved.getName());
    }

    @Transactional(readOnly = true)
    public List<ExternalProjectResponse> getExternalProjects(Long userId) {
        log.info("Fetching external projects for user {}", userId);
        User user = findUserOrThrow(userId);

        return externalProjectRepository.findByUser(user)
                .stream()
                .map(p -> new ExternalProjectResponse(p.getId(), p.getName()))
                .toList();
    }

    private User findUserOrThrow(Long id) {
        log.info("Fetching user: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User %d not found".formatted(id)));
    }

    private UserResponse toUserResponse(User user) {
        log.info("Fetching user: {}", user);
        var projects = user.getExternalProjects().stream()
                .map(p -> new ExternalProjectResponse(p.getId(), p.getName()))
                .toList();

        return new UserResponse(user.getId(), user.getEmail(), user.getName(), projects);
    }

    @Transactional(readOnly = true)
    public List<ShortUserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(u -> new ShortUserResponse(u.getId(), u.getEmail(), u.getName()))
                .toList();


    }
}
