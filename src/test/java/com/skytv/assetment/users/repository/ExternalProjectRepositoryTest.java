package com.skytv.assetment.users.repository;

import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExternalProjectRepositoryTest {

    @Autowired
    private ExternalProjectRepository externalProjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("usersdb")
            .withUsername("users")
            .withPassword("userspass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.liquibase.enabled", () -> true);
    }

    @Test
    void createExternalProject_addNewExternalProjectSuccessfully() {

        User user = User.builder()
                .email("user@test.com")
                .password("pwd")
                .name("Test User")
                .build();

        user = userRepository.save(user);
        var externalProject = ExternalProject.builder().name("Test").build();
        externalProject.addUser(user);
        externalProjectRepository.save(externalProject);

        var saved = externalProjectRepository.findById(externalProject.getId());
        assertThat(saved).isPresent();
        assertThat(saved.get().getId()).isNotNull();
        assertThat(saved.get().getUsers()).hasSize(1);

    }

    @Test
    void listAllExternalProjectsByUser() {

        User user = User.builder()
                .email("user@test.com")
                .password("pwd")
                .name("Test User")
                .build();

        val saved  = userRepository.save(user);


        IntStream.range(0, 10).forEach(index -> {
            var externalProject = ExternalProject.builder().name("Project " + index).build();
            externalProject.addUser(saved);
            externalProjectRepository.save(externalProject);
        });

        var myprojects = externalProjectRepository.findByUser(saved);
        assertThat(myprojects).isNotEmpty();
        assertThat(myprojects).hasSize(10);



    }
}
