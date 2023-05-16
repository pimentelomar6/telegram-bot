package com.pimentelprojects.productivitymaster.services;

import com.pimentelprojects.productivitymaster.models.Task;
import com.pimentelprojects.productivitymaster.models.UserEntity;

import java.util.List;

public interface TaskService {

    void createTask(Task task);
    void deleteTaskById(Long id);
    Task getTaskById(Long id);
    List<Task> getAllTask(UserEntity entity);

    boolean existById(Long id);

}
