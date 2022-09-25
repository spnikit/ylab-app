package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    //Полная замена User на предоставленного нового User, для частичной использовать Patch?
    @Override
    public UserDto updateUser(UserDto userDto) {

        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Updated user: {}", user);
        userRepository
                .findByIdForUpdate(user.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + userDto.getId() + " not found"));

        Person updatedUser = userRepository.save(user);

        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {

        Optional<Person> optionalUserById = userRepository.findById(id);
        return optionalUserById
                .map(userMapper::personToUserDto)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @Override
    public void deleteUserById(Long id) {

        // Читал про несколько подходов обработки случая, когда удаляемый объект по запрашиваему ID не найден,
        // выбрал молча ловить Exception, не предупреждая пользователя, что объетка не было изначально;
        // источник информации: Spring in Action 6th edition, Manning, глава 7.1.4
        try {
            userRepository.deleteById(id);

        } catch (EmptyResultDataAccessException exception) {
            //NOP
        }
        log.info("User with id: {} deleted", id);
    }
}
