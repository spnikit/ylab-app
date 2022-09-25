package com.edu.ulab.app;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql({"/schema.sql", "/data.sql"})
public class BookServiceImplTemplateTest {

    private BookServiceImplTemplate bookServiceImplTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initTests() {
        bookServiceImplTemplate = new BookServiceImplTemplate(jdbcTemplate);

    }


    @Test
    void shouldCreateBookAndReturnCreatedBookWhenQueryItById() {

        BookDto bookDto = new BookDto();

        bookDto.setTitle("updated title");
        bookDto.setAuthor("updated author");
        bookDto.setPageCount(666);
        bookDto.setUserId(1001L);

        BookDto createdBook = bookServiceImplTemplate.createBook(bookDto);

        assertNotNull(bookServiceImplTemplate.getBookById(createdBook.getId()));

    }

    @Test
    void whenUpdateBookByIdShouldReturnUpdatedBook() {

        BookDto bookDto = new BookDto();

        bookDto.setId(3003L);
        bookDto.setTitle("updated title");
        bookDto.setAuthor("updated author");
        bookDto.setPageCount(666);
        bookDto.setUserId(1001L);

        BookDto updatedBookDto = bookServiceImplTemplate.updateBook(bookDto);

        assertEquals(3003, updatedBookDto.getId());
        assertEquals("updated title", updatedBookDto.getTitle());
        assertEquals("updated author", updatedBookDto.getAuthor());
        assertEquals(666, updatedBookDto.getPageCount());
        assertEquals(1001, updatedBookDto.getUserId());

    }

    @Test
    void whenGetBookByIdThenReturnCorrectBook() {

        BookDto bookDtoById = bookServiceImplTemplate.getBookById(2002L);

        assertEquals(2002, bookDtoById.getId());
        assertEquals("default book", bookDtoById.getTitle());
        assertEquals("author", bookDtoById.getAuthor());
        assertEquals(5500, bookDtoById.getPageCount());
        assertEquals(1001, bookDtoById.getUserId());
    }

    @Test
    void whenDeleteBookByIdThenThrowExceptionIfTryToGetIt() {

        bookServiceImplTemplate.deleteBookById(2002L);

        assertThrows(NotFoundException.class, () -> bookServiceImplTemplate.getBookById(23L));
    }

}
