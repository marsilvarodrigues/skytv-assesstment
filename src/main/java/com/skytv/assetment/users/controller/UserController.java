package com.skytv.assetment.users.controller;

import com.skytv.assetment.users.dto.ExternalProjectRequest;
import com.skytv.assetment.users.dto.ExternalProjectResponse;
import com.skytv.assetment.users.dto.UserRequest;
import com.skytv.assetment.users.dto.UserResponse;
import com.skytv.assetment.users.service.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user")
    @Timed(value = "users.create.time", description = "Time taken to create a user")
    @Counted(value = "users.create.count", description = "Number of calls to create user")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) {
        log.info("Create user request: {}", request);
        return userService.createUser(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @Timed(value = "users.get.time", description = "Time taken to get a user")
    @Counted(value = "users.get.count", description = "Number of calls to get user")
    public UserResponse getUser(@PathVariable Long id) {
        log.info("Get user by ID: {}", id);
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user")
    @Timed(value = "users.update.time", description = "Time taken to update a user")
    @Counted(value = "users.update.count", description = "Number of calls to update user")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UserRequest request) {
        log.info("Update user request: {}", request);
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user by ID")
    @Timed(value = "users.delete.time", description = "Time taken to delete a user")
    @Counted(value = "users.delete.count", description = "Number of calls to delete user")
    public void deleteUser(@PathVariable Long id)
    {
        log.info("Delete user by ID: {}", id);
        userService.deleteUser(id);
    }

    @PostMapping("/{id}/external-projects")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add an external project to a user")
    @Timed(value = "users.external-projects.add.time", description = "Time taken to add external project")
    @Counted(value = "users.external-projects.add.count", description = "Number of calls to add external project")
    public ExternalProjectResponse addExternalProject(@PathVariable Long id,
                                                      @Valid @RequestBody ExternalProjectRequest request) {
        log.info("Add external project to a user: {}", request);
        return userService.addExternalProject(id, request);
    }

    @GetMapping("/{id}/external-projects")
    @Operation(summary = "List external projects for a user")
    @Timed(value = "users.external-projects.list.time", description = "Time taken to list external projects")
    @Counted(value = "users.external-projects.list.count", description = "Number of calls to list external projects")
    public List<ExternalProjectResponse> getExternalProjects(@PathVariable Long id) {
        log.info("List external projects for a user: {}", id);
        return userService.getExternalProjects(id);
    }
}
