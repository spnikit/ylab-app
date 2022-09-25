package com.edu.ulab.app;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@Sql({"/schema.sql", "/data.sql"})
public class UserServiceImplTemplateTest {

    private UserServiceImplTemplate userServiceImplTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initTests() {
        userServiceImplTemplate = new UserServiceImplTemplate(jdbcTemplate);

    }


    @Test
    void whenGetUserByIdThenReturnCorrectUser() {

        UserDto userDtoById = userServiceImplTemplate.getUserById(1001L);

        assertEquals(1001, userDtoById.getId());
        assertEquals("reader", userDtoById.getTitle());
        assertEquals(55, userDtoById.getAge());
        assertEquals("default user", userDtoById.getFullName());
    }

}
