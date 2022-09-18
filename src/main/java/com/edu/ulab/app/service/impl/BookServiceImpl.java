package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.exception.StorageException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.StorageUtils;
import com.edu.ulab.app.storage.repositories.BookRepo;
import com.edu.ulab.app.storage.repositories.impl.BookRepoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookrepo;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepoImpl bookRepo, BookMapper bookMapper) {
        this.bookrepo = bookRepo;
        this.bookMapper = bookMapper;
    }


    @Override
    public BookDto createBook(BookDto bookDto) {

        Long id = StorageUtils.generateId();
        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        bookEntity.setId(id);

        BookEntity createdBookEntity = bookrepo.create(bookEntity);


        return bookMapper.bookEntityToBookDto(createdBookEntity);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);

        BookEntity updatedBookEntity = bookrepo.update(bookEntity);

        return bookMapper.bookEntityToBookDto(updatedBookEntity);
    }


    public List<BookDto> getBooks() {
        return StreamSupport.stream(bookrepo.findAll().spliterator(), false)
                .filter(Objects::nonNull)
                .map(bookMapper::bookEntityToBookDto)
                .toList();

    }

    @Override
    public BookDto getBookById(Long id) {

        Optional<BookEntity> optionalBookEntity = bookrepo.getById(id);

        return optionalBookEntity
                .map(bookMapper::bookEntityToBookDto)
                .orElseThrow(() -> new StorageException("Book with id " + id + " not found"));
    }

    @Override
    public void deleteBookById(Long id) {
        bookrepo.deleteById(id);
    }
}
