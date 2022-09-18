package com.edu.ulab.app.storage.repositories.impl;

import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.exception.StorageException;
import com.edu.ulab.app.storage.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
@Slf4j
public class UserRepoImpl implements UserRepo {

    //TODO: pick another type of map Concurrent for example?
    private final Map<Long, UserEntity> users = new HashMap<>();


    @Override
    public UserEntity create(UserEntity entity) {

        users.put(entity.getId(), entity);

        UserEntity userEntity = users.get(entity.getId());

        log.info("Created userEntity: {}", userEntity);

        return userEntity;

    }

    @Override
    public UserEntity update(UserEntity entity) {

        Long userId = entity.getId();

        if (!users.containsKey(userId)) {
            throw new StorageException("User with id " + userId + " not found");
        }

        UserEntity userEntity = users.put(entity.getId(), entity);

        log.info("Updated userEntity, old value is: {}", userEntity);

        return users.put(entity.getId(), entity);
    }

    @Override
    public Optional<UserEntity> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        UserEntity deletedUserEntity = users.remove(id);

        log.info("Deleted userEntity with: {}", deletedUserEntity);

    }
}
