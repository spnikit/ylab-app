package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {

        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME=?, TITLE=?, AGE=? WHERE ID=?";

        int numberOfRowsUpdated = jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(),
                userDto.getTitle(), userDto.getAge(), userDto.getId());

        if (numberOfRowsUpdated == 0) {
            log.info("Update Person, UserDto: {} 0 rows were updated!", userDto);
        }

        return this.getUserById(userDto.getId());
    }

    @Override
    public UserDto getUserById(Long id) {
        String GET_BY_ID_SQL = "SELECT ID, FULL_NAME, TITLE, AGE FROM PERSON WHERE ID=?";

        List<UserDto> userDtoList = jdbcTemplate.query(GET_BY_ID_SQL, this::mapRowToUseDto, id);

        if (userDtoList.isEmpty()) {
            throw (new NotFoundException("Get Book By ID - Book with id:" + id + " was not found!"));
        } else if (userDtoList.size() == 1) {
            return userDtoList.get(0);
        } else {
            // list contains more than 1 element
            throw (new NotFoundException("Get Book By ID - Book with id: " + id + " contains more than 1 book"));
        }

    }

    @Override
    public void deleteUserById(Long id) {
        String DELETE_SQL = "DELETE FROM PERSON WHERE ID=?";

        jdbcTemplate.update(DELETE_SQL, id);
    }


    @SneakyThrows
    private UserDto mapRowToUseDto(ResultSet resultSet, int rowNum) {
        UserDto userDto = new UserDto();

        userDto.setId(resultSet.getLong("ID"));
        userDto.setAge(resultSet.getInt("AGE"));
        userDto.setFullName(resultSet.getString("FULL_NAME"));
        userDto.setTitle(resultSet.getString("TITLE"));

        return userDto;
    }
}
