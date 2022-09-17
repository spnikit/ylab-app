package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.StorageUtils;
import com.edu.ulab.app.storage.repositories.impl.BookRepo;
import com.edu.ulab.app.storage.repositories.impl.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepo userRepo;

    public UserServiceImpl(UserMapper mapper, BookRepo bookRepo, UserRepo userRepo) {
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
        return null;
    }

    @Override
    public UserDto getUserById(Long id) {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {

    }
}
