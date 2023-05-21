package com.pimentelprojects.productivitymaster.services.impl;

import com.pimentelprojects.productivitymaster.models.UserEntity;

import com.pimentelprojects.productivitymaster.repository.UserEntityRepository;
import com.pimentelprojects.productivitymaster.services.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserEntityServiceImpl implements UserEntityService {

    private final UserEntityRepository userEntityRepository;


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
