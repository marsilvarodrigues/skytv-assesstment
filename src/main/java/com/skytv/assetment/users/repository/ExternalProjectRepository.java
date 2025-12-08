package com.skytv.assetment.users.repository;

import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalProjectRepository extends JpaRepository<ExternalProject, String> {

    List<ExternalProject> findByUser(User user);
}
