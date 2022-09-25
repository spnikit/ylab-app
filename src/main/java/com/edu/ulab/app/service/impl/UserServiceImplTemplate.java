package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userDto.getId());

        return this.getUserById(userDto.getId());
    }

    @Override
    public UserDto getUserById(Long id) {
        String GET_BY_ID_SQL = "SELECT ID, FULL_NAME, TITLE, AGE FROM PERSON WHERE ID=?";

        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, this::mapRowToUseDto, id);
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
