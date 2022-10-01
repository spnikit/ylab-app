package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then
        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    // failed create
    @Test
    @DisplayName("Создание пользователя c null. Должно выбросить ошибку")
    void createPersonWithNull_Test(){

        assertThatThrownBy(() -> userService.updateUser(null))
                .isInstanceOf(NullPointerException.class);
    }

    // update
    @Test
    @DisplayName("Обновление пользователя.")
    void updatePerson_Test() {
        // given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setId(1L);

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");
        person.setId(1L);

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        // when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.findByIdForUpdate(person.getId())).thenReturn(Optional.of(person));
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        // then
        UserDto userDtoResult = userService.updateUser(userDto);
        assertThat(userDtoResult.getId()).isEqualTo(1L);
    }

    // failed update
    @Test
    @DisplayName("Обновленипе пользователя c null. Должно выбросить ошибку")
    void updatePersonWithNull_Test(){

        assertThatThrownBy(() -> userService.updateUser(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("не может быть null");

    }



    @Test
    @DisplayName("Получение пользователя.")
    void getPersonById_Test() {
        // given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setId(1L);

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");
        person.setId(1L);

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(result);

        // then
        UserDto userById = userService.getUserById(1L);
        assertThat(userById.getId()).isEqualTo(1L);
    }

    // failed get by id
    @Test
    @DisplayName("Получение пользователя c null. Должно выбросить ошибку")
    void getPersonWithNullId_Test(){

        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(NotFoundException.class);
    }


    // delete
    @Test
    @DisplayName("Удаление пользователя.")
    void deletePerson_Test() {
        // given

        // when
        userService.deleteUserById(1L);

        // then
        verify(userRepository).deleteById(1L);
    }





    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");
}
