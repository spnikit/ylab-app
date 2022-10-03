package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    @Transactional
    public UserBookResponse updateUserWithBooks(Long userId, UserBookRequest userBookRequest) {
        log.info("Got user book update request:{}, user id: {}, ", userBookRequest, userId);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUserDto = userService.updateUser(userDto);


        log.info("Delete old books of userId: {}", userId);

        bookService.getBooks().stream()
                .filter(Objects::nonNull)
                .filter(bookDto -> userId.equals(bookDto.getUserId()))
                .peek(deleteBookDto -> log.info("Deleted book: {} of the user id: {}", deleteBookDto, userId))
                .forEach(deleteBookDto -> bookService.deleteBookById(deleteBookDto.getId()));


        log.info("Updated userId: {} book list", userId);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(updatedUserDto.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(updatedBook -> log.info("Updated book: {}", updatedBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);


        return UserBookResponse.builder()
                .userId(userId)
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user book get by id request, user id: {}", userId);

        UserDto userById = userService.getUserById(userId);

        List<Long> bookIdList = bookService.getBooks().stream()
                .filter(Objects::nonNull)
                .filter(bookDto -> userId.equals(bookDto.getUserId()))
                .peek(filteredBookDto -> log.info("Get book: {} of the user id: {}", filteredBookDto, userId))
                .map(BookDto::getId)
                .toList();


        return UserBookResponse.builder()
                .userId(userById.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Got user book delete request, user id: {}", userId);

        bookService.getBooks().stream()
                .filter(Objects::nonNull)
                .filter(bookDto -> userId.equals(bookDto.getUserId()))
                .peek(deleteBookDto -> log.info("Deleted book: {} of the user id: {}", deleteBookDto, userId))
                .forEach(deleteBookDto -> bookService.deleteBookById(deleteBookDto.getId()));


        userService.deleteUserById(userId);
    }
}
