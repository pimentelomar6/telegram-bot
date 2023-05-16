package com.pimentelprojects.productivitymaster.services;

import com.pimentelprojects.productivitymaster.models.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserEntityService {

    List<String> getAllTasks(Long id);
    UserEntity getById(Long id);

    void createUser(UserEntity entity);

    boolean existById(Long id);
}
