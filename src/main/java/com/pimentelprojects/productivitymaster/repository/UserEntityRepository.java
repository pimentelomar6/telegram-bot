package com.pimentelprojects.productivitymaster.repository;

import com.pimentelprojects.productivitymaster.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
}
