package com.pimentelprojects.productivitymaster.services;

import com.pimentelprojects.productivitymaster.models.UserEntity;




public interface UserEntityService {

    UserEntity getById(Long id);

    void createUser(UserEntity entity);

    boolean existById(Long id);
}
