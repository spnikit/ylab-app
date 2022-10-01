package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        userRepository.findById(bookDto.getUserId())
                .ifPresent(book::setPerson);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Updated book: {}", book);
        bookRepository
                .findByIdForUpdate(book.getId())
                .orElseThrow(() -> new NotFoundException("User with id: " + bookDto.getId() + " not found"));

        Book updatedBook = bookRepository.save(book);

        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> optionalBookById = bookRepository.findById(id);
        return optionalBookById
                .map(bookMapper::bookToBookDto)
                .orElseThrow(() -> new NotFoundException("Book with id " + id + " not found"));
    }

    public List<BookDto> getBooks() {

        return bookRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        try {

            bookRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            //NOP
        }
        log.info("Book with id: {} deleted", id);
    }
}

