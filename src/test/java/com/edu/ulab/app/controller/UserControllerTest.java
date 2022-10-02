package com.edu.ulab.app.controller;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.request.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers
public class UserControllerTest {


    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13-alpine");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BookServiceImpl bookService;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }


    @Test
    public void contextLoads() {
    }


    @Test
    @SneakyThrows
    public void testCreateUserWithBooks() {

        UserBookRequest userBookRequest = new UserBookRequest();

        UserRequest userRequest = new UserRequest();
        userRequest.setTitle("reader");
        userRequest.setAge(33);
        userRequest.setFullName("test");

        BookRequest bookRequest1 = new BookRequest();
        bookRequest1.setTitle("book name");
        bookRequest1.setAuthor("test author");
        bookRequest1.setPageCount(222);

        BookRequest bookRequest2 = new BookRequest();
        bookRequest2.setTitle("book name test");
        bookRequest2.setAuthor("test author second");
        bookRequest2.setPageCount(555);

        userBookRequest.setUserRequest(userRequest);
        userBookRequest.setBookRequests(List.of(bookRequest1, bookRequest2));


        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(post("/api/v1/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("rqid", "requestId1010101")
                        .content(mapper.writeValueAsString(userBookRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", greaterThan(0)))
                .andExpect(jsonPath("$.booksIdList", hasSize(2)))
                .andReturn()
                .getResponse();

        Integer id = JsonPath.parse(response.getContentAsString()).read("$.userId");

        Assertions.assertNotNull(userService.getUserById(Long.valueOf(id)));


    }

    @Test
    @SneakyThrows
    public void getUserWithBooksById() {
        UserDto userDto = new UserDto();
        userDto.setTitle("test");
        userDto.setAge(33);
        userDto.setFullName("petrovich");
        UserDto createdUser = userService.createUser(userDto);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(createdUser.getId());
        bookDto.setTitle("book title");
        bookDto.setAuthor("author petrovich");
        bookDto.setPageCount(222);
        BookDto createdBook = bookService.createBook(bookDto);


        mockMvc.perform(get("/api/v1/user/get/" + createdUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", equalTo(createdUser.getId().intValue())))
                .andExpect(jsonPath("$.booksIdList", hasSize(1)))
                .andExpect(jsonPath("$.booksIdList", contains(createdBook.getId().intValue())));
    }

    @Test
    @SneakyThrows
    public void updateUserWithBooks() {
        UserBookRequest userBookRequest = new UserBookRequest();


        UserDto userDto = new UserDto();
        userDto.setTitle("test_update");
        userDto.setAge(33);
        userDto.setFullName("petrovich");
        UserDto createdUser = userService.createUser(userDto);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(createdUser.getId());
        bookDto.setTitle("book title");
        bookDto.setAuthor("author petrovich");
        bookDto.setPageCount(222);
        bookService.createBook(bookDto);

        ObjectMapper mapper = new ObjectMapper();

        UserRequest updatedUserRequest = new UserRequest();
        updatedUserRequest.setTitle("testUpdated");
        updatedUserRequest.setAge(66);
        updatedUserRequest.setFullName("ivanovich");


        userBookRequest.setUserRequest(updatedUserRequest);
        userBookRequest.setBookRequests(Collections.emptyList());

        mockMvc.perform(put("/api/v1/user/update/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userBookRequest)))
                .andDo(print())
                .andExpect(status().isOk());


        UserDto userById = userService.getUserById(createdUser.getId());

        Assertions.assertEquals(userById.getTitle(), "testUpdated");
        Assertions.assertEquals(userById.getAge(), 66);
        Assertions.assertEquals(userById.getFullName(), "ivanovich");

    }

    @Test
    @SneakyThrows
    public void testDeleteUserWithBooks() {

        UserDto userDto = new UserDto();
        userDto.setTitle("test_delete");
        userDto.setAge(33);
        userDto.setFullName("petrovich");
        userDto.setId(1L);
        UserDto createdUser = userService.createUser(userDto);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(createdUser.getId());
        bookDto.setTitle("book title");
        bookDto.setAuthor("author petrovich");
        bookDto.setPageCount(222);
        BookDto createdBook = bookService.createBook(bookDto);


        mockMvc.perform(delete("/api/v1/user/delete/" + createdUser.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(userDto.getId()));
        Assertions.assertThrows(NotFoundException.class, () -> bookService.getBookById(createdBook.getId()));

    }
}
