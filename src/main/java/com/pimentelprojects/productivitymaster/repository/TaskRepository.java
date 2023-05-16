package com.pimentelprojects.productivitymaster.repository;

import com.pimentelprojects.productivitymaster.models.Task;
import com.pimentelprojects.productivitymaster.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserEntity(UserEntity entity);
}
