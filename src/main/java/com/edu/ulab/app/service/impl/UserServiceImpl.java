package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.StorageUtils;
import com.edu.ulab.app.storage.repositories.UserRepo;
import com.edu.ulab.app.storage.repositories.impl.BookRepoImpl;
import com.edu.ulab.app.storage.repositories.impl.UserRepoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserServiceImpl(UserMapper mapper, BookRepoImpl bookRepo, UserRepoImpl userRepo) {
        this.userMapper = mapper;
        this.userRepo = userRepo;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // сгенерировать идентификатор
        Long id = StorageUtils.generateId();

        // создать пользователя
        UserEntity entity = userMapper.userDtoToUserEntity(userDto);
        entity.setId(id);
        UserEntity createdUserEntity = userRepo.create(entity);

        // вернуть сохраненного пользователя со всеми необходимыми полями id
        return userMapper.userEntityToUserDto(createdUserEntity);

    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        UserEntity entity = userMapper.userDtoToUserEntity(userDto);

        UserEntity updatedEnity = userRepo.update(entity);


        return userMapper.userEntityToUserDto(updatedEnity);
    }

    @Override
    public UserDto getUserById(Long id) {

        Optional<UserEntity> optionalEntity = userRepo.getById(id);

        //TODO: return null or Throw?

        return optionalEntity
                .map(userMapper::userEntityToUserDto)
                .orElse(null);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }
}
