package com.skytv.assetment.users.controller;

import com.skytv.assetment.users.dto.FullExternalProjectResponse;
import com.skytv.assetment.users.service.ExternalProjectService;
import com.skytv.assetment.users.dto.ExternalProjectResponse;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external-projects")
@RequiredArgsConstructor
@Tag(name = "External Projects", description = "External Projects management API")
@Slf4j
public class ExternalProjectController {

    private final ExternalProjectService externalProjectService;

    @GetMapping("/{id}")
    @Operation(summary = "Get external project by ID")
    @Timed(value = "external-projects.get.time", description = "Time taken to get a external project")
    @Counted(value = "external-projects.get.count", description = "Number of calls to get external project")
    public ResponseEntity<FullExternalProjectResponse> getExternalProject(@PathVariable String id) {
        log.info("Get external project by ID: {}", id);
        return ResponseEntity.ok(externalProjectService.getExternalProject(id));
    }

    @GetMapping("/")
    @Operation(summary = "Get external project by ID")
    @Timed(value = "external-projects.get.time", description = "Time taken to get all externals project")
    @Counted(value = "external-projects.get.count", description = "Number of calls to get all external projects")
    public ResponseEntity<List<ExternalProjectResponse>> getAllExternalProjects() {
        log.info("Get all external projects");
        return ResponseEntity.ok(externalProjectService.getAllExternalProjects());
    }
}
