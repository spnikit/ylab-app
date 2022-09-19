package com.edu.ulab.app;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.StorageException;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

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
    public void testDeleteUserWithBooks() {

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


        mockMvc.perform(delete("/api/v1/user/delete/" + createdUser.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThrows(StorageException.class, () -> userService.getUserById(userDto.getId()));
        Assertions.assertThrows(StorageException.class, () -> bookService.getBookById(createdBook.getId()));

    }
}
