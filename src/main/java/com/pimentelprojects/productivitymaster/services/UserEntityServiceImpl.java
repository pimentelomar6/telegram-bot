package com.pimentelprojects.productivitymaster.services;

import com.pimentelprojects.productivitymaster.models.UserEntity;
import com.pimentelprojects.productivitymaster.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService{

    private final UserEntityRepository userEntityRepository;

    @Override
    public List<String> getAllTasks(Long id) {
        UserEntity user = userEntityRepository.findById(id).get();

        return user.getTasks();
    }

    @Override
    public UserEntity getById(Long id) {
        return  userEntityRepository.findById(id).get();
    }

    @Override
    public void createUser(UserEntity entity) {
        userEntityRepository.save(entity);
    }

    @Override
    public boolean existById(Long id) {
        return userEntityRepository.existsById(id);
    }
}
