package com.edu.ulab.app.storage.repositories.impl;

import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.storage.repositories.CrudRepo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
public class UserRepo implements CrudRepo<UserEntity> {

    private Map<Long, UserEntity> users = new HashMap<>();


    @Override
    public UserEntity create(UserEntity entity) {

        UserEntity userEntity = users.put(entity.getId(), entity);

        return users.get(entity.getId());

        //TODO: don't forget about logging
    }

    @Override
    public UserEntity update(UserEntity entity) {
        return users.put(entity.getId(), entity);
//TODO: add additional logic
    }

    @Override
    public Optional<UserEntity> getById(Long id) {
        return Optional.of(users.get(id));
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);

        //TODO: add more logic
    }
}
