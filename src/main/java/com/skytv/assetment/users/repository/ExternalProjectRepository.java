package com.skytv.assetment.users.repository;

import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExternalProjectRepository extends JpaRepository<ExternalProject, String> {

    @Query("SELECT ep FROM ExternalProject ep JOIN ep.users u WHERE u = :user")
    List<ExternalProject> findByUser(@Param("user") User user);
}
