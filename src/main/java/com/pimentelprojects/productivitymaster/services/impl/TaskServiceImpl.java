package com.pimentelprojects.productivitymaster.services.impl;

import com.pimentelprojects.productivitymaster.models.Task;
import com.pimentelprojects.productivitymaster.models.UserEntity;
import com.pimentelprojects.productivitymaster.repository.TaskRepository;
import com.pimentelprojects.productivitymaster.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    @Override
    public void createTask(Task task) {
        taskRepository.save(task);
    }

    @Override
    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).get();
    }

    @Override
    public List<Task> getAllTask(UserEntity entity) {
        return taskRepository.findByUserEntity(entity);
    }

    @Override
    public boolean existById(Long id) {
        return taskRepository.existsById(id);
    }


}
