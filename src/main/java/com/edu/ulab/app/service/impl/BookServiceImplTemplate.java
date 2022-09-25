package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.service.BookService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PAGE_COUNT=?, USER_ID=? WHERE ID=?";

        jdbcTemplate.update(UPDATE_SQL, bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount(),
                bookDto.getUserId(), bookDto.getId());

        return this.getBookById(bookDto.getId());
    }

    @Override
    public BookDto getBookById(Long id) {
        String GET_BY_ID_SQL = "SELECT ID, TITLE, AUTHOR, PAGE_COUNT, USER_ID FROM BOOK WHERE ID=?";

        return jdbcTemplate.queryForObject(GET_BY_ID_SQL, BookDto.class, id);
    }

    @Override
    public List<BookDto> getBooks() {
        String SELECT_ALL_SQL = "SELECT ID, TITLE, AUTHOR, PAGE_COUNT, USER_ID FROM BOOK";

        return jdbcTemplate.query(SELECT_ALL_SQL, this::mapRowToBookDto);
    }

    @Override
    public void deleteBookById(Long id) {
        String DELETE_SQL = "DELETE FROM BOOK WHERE ID=?";

        jdbcTemplate.update(DELETE_SQL, id);
    }

    @SneakyThrows
    private BookDto mapRowToBookDto(ResultSet resultSet, int rowNum) {

        BookDto bookDto = new BookDto();

        bookDto.setId(resultSet.getLong("ID"));
        bookDto.setTitle(resultSet.getString("TITLE"));
        bookDto.setAuthor(resultSet.getString("AUTHOR"));
        bookDto.setPageCount(resultSet.getLong("PAGE_COUNT"));
        bookDto.setUserId(resultSet.getLong("USER_ID"));

        return bookDto;
    }
}
