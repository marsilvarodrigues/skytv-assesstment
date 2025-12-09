package com.skytv.assetment.users.service;

import com.skytv.assetment.users.dto.ExternalProjectResponse;
import com.skytv.assetment.users.dto.FullExternalProjectResponse;
import com.skytv.assetment.users.dto.ShortUserResponse;
import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.exception.ResourceNotFoundException;
import com.skytv.assetment.users.repository.ExternalProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalProjectService {
    private final ExternalProjectRepository externalProjectRepository;

    public FullExternalProjectResponse getExternalProject(String id) {
        log.info("Get external project by ID {}", id);
        ExternalProject externalProject = externalProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("External Project %s not found".formatted(id)));

        return new FullExternalProjectResponse(externalProject.getId(),  externalProject.getName(),
                externalProject.getUsers()
                        .stream()
                        .map(e -> new ShortUserResponse(e.getId(), e.getEmail(), e.getName()))
                        .toList());
    }

    public List<ExternalProjectResponse> getAllExternalProjects() {
        log.info("Get all external projects");
        return externalProjectRepository.findAll()
                .stream()
                .map(p -> new ExternalProjectResponse(p.getId(), p.getName()))
                .toList();
    }
}
